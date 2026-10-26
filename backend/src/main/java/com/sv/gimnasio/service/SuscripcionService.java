package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.PlanDAO;
import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.dao.PagoDAO;
import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dto.SuscripcionRequestDTO;
import com.sv.gimnasio.model.Pago;
import com.sv.gimnasio.model.Plan;
import com.sv.gimnasio.model.Suscripcion;
import com.sv.gimnasio.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Logica de negocio de suscripciones (HU-002). La vigencia se consulta en la
 * base de datos para mantener una unica fuente de verdad y evitar que una
 * caché conserve cambios que hayan sido revertidos por una transaccion.
 */
@Service
public class SuscripcionService {

    private final SuscripcionDAO suscripcionDAO;
    private final PlanDAO planDAO;
    private final PagoDAO pagoDAO;
    private final ClienteDAO clienteDAO;
    private final Clock reloj;
    private final IdGenerator idGenerator;

    public SuscripcionService(SuscripcionDAO suscripcionDAO, PlanDAO planDAO,
                               PagoDAO pagoDAO, ClienteDAO clienteDAO, Clock reloj,
                               IdGenerator idGenerator) {
        this.suscripcionDAO = suscripcionDAO;
        this.planDAO = planDAO;
        this.pagoDAO = pagoDAO;
        this.clienteDAO = clienteDAO;
        this.reloj = reloj;
        this.idGenerator = idGenerator;
    }

    public List<Suscripcion> listarPorCliente(String idCliente) {
        return suscripcionDAO.obtenerPorCliente(idCliente);
    }

    /**
     * HU-002: vende un plan (mensual o anual) a un cliente. El calculo de la
     * fecha de fin y del precio final se delega en el propio Plan
     * (POLIMORFISMO: PlanMensual y PlanAnual calculan distinto).
     */
    @Transactional // suscripcion y pago se guardan juntos o no se guarda ninguno
    public Suscripcion venderPlan(SuscripcionRequestDTO dto) {
        clienteDAO.obtenerPorId(dto.getIdCliente())
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado: " + dto.getIdCliente()));

        Plan plan = planDAO.obtenerPorId(dto.getIdPlan())
                .orElseThrow(() -> new NoSuchElementException("Plan no encontrado: " + dto.getIdPlan()));

        LocalDate inicio = LocalDate.now(reloj);
        LocalDate fin = plan.calcularFechaFin(inicio);

        Suscripcion suscripcion = new Suscripcion(
                idGenerator.generar("SUS"),
                dto.getIdCliente(), plan.getIdPlan(), inicio, fin, "ACTIVA");
        suscripcionDAO.guardar(suscripcion);

        Pago pago = new Pago(idGenerator.generar("PAG"),
                suscripcion.getIdSuscripcion(), LocalDateTime.now(reloj), plan.precioFinal(),
                dto.getMetodoPago() == null ? "EFECTIVO" : dto.getMetodoPago());
        pagoDAO.guardar(pago);

        return suscripcion;
    }

    /** Usado por AccesoService (HU-003) para validar el ingreso por QR. */
    public boolean tieneSuscripcionActiva(String idCliente) {
        LocalDate hoy = LocalDate.now(reloj);
        return suscripcionDAO.obtenerPorCliente(idCliente).stream()
                .anyMatch(suscripcion -> "ACTIVA".equals(suscripcion.getEstado())
                        && suscripcion.getFechaFin() != null
                        && !suscripcion.getFechaFin().isBefore(hoy));
    }
}
