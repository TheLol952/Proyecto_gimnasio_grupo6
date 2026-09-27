package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.PlanDAO;
import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.dao.PagoDAO;
import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dto.SuscripcionRequestDTO;
import com.sv.gimnasio.model.Pago;
import com.sv.gimnasio.model.Plan;
import com.sv.gimnasio.model.Suscripcion;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Logica de negocio de suscripciones (HU-002). Usa una COLECCION HashMap
 * indexada por idCliente para responder en O(1) si el cliente tiene una
 * suscripcion vigente, algo que se consulta constantemente desde el control
 * de acceso por QR simulado (HU-003).
 */
@Service
public class SuscripcionService {

    private final SuscripcionDAO suscripcionDAO;
    private final PlanDAO planDAO;
    private final PagoDAO pagoDAO;
    private final ClienteDAO clienteDAO;

    /** idCliente -> ultima suscripcion registrada. */
    private final Map<String, Suscripcion> suscripcionPorCliente = new HashMap<>();

    public SuscripcionService(SuscripcionDAO suscripcionDAO, PlanDAO planDAO, PagoDAO pagoDAO, ClienteDAO clienteDAO) {
        this.suscripcionDAO = suscripcionDAO;
        this.planDAO = planDAO;
        this.pagoDAO = pagoDAO;
        this.clienteDAO = clienteDAO;
    }

    @PostConstruct
    public void init() {
        suscripcionDAO.obtenerTodos().forEach(s -> suscripcionPorCliente.put(s.getIdCliente(), s));
    }

    public List<Suscripcion> listarPorCliente(String idCliente) {
        return suscripcionDAO.obtenerPorCliente(idCliente);
    }

    /**
     * HU-002: vende un plan (mensual o anual) a un cliente. El calculo de la
     * fecha de fin y del precio final se delega en el propio Plan
     * (POLIMORFISMO: PlanMensual y PlanAnual calculan distinto).
     */
    public Suscripcion venderPlan(SuscripcionRequestDTO dto) {
        clienteDAO.obtenerPorId(dto.getIdCliente())
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado: " + dto.getIdCliente()));

        Plan plan = planDAO.obtenerPorId(dto.getIdPlan())
                .orElseThrow(() -> new NoSuchElementException("Plan no encontrado: " + dto.getIdPlan()));

        LocalDate inicio = LocalDate.now();
        LocalDate fin = plan.calcularFechaFin(inicio);

        Suscripcion suscripcion = new Suscripcion(
                "SUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                dto.getIdCliente(), plan.getIdPlan(), inicio, fin, "ACTIVA");
        suscripcionDAO.guardar(suscripcion);
        suscripcionPorCliente.put(dto.getIdCliente(), suscripcion);

        Pago pago = new Pago("PAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                suscripcion.getIdSuscripcion(), LocalDateTime.now(), plan.precioFinal(),
                dto.getMetodoPago() == null ? "EFECTIVO" : dto.getMetodoPago());
        pagoDAO.guardar(pago);

        return suscripcion;
    }

    /** Usado por AccesoService (HU-003) para validar el ingreso por QR. */
    public boolean tieneSuscripcionActiva(String idCliente) {
        Suscripcion suscripcion = suscripcionPorCliente.get(idCliente);
        return suscripcion != null
                && "ACTIVA".equals(suscripcion.getEstado())
                && !suscripcion.getFechaFin().isBefore(LocalDate.now());
    }

    public Map<String, Suscripcion> obtenerCacheSuscripcionesActivas() {
        return suscripcionPorCliente;
    }
}
