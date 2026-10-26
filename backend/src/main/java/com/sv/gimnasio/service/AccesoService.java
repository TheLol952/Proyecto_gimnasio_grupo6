package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.AccesoDAO;
import com.sv.gimnasio.dto.AccesoRequestDTO;
import com.sv.gimnasio.model.Acceso;
import com.sv.gimnasio.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** HU-003: control de acceso mediante codigo QR simulado. */
@Service
public class AccesoService {

    private final AccesoDAO accesoDAO;
    private final SuscripcionService suscripcionService;
    private final Clock reloj;
    private final IdGenerator idGenerator;

    public AccesoService(AccesoDAO accesoDAO, SuscripcionService suscripcionService,
                         Clock reloj, IdGenerator idGenerator) {
        this.accesoDAO = accesoDAO;
        this.suscripcionService = suscripcionService;
        this.reloj = reloj;
        this.idGenerator = idGenerator;
    }

    public Acceso registrarIntento(AccesoRequestDTO dto) {
        String idCliente = dto.getIdCliente().trim().toUpperCase();
        String codigoRecibido = dto.getCodigoQR().trim().toUpperCase();
        boolean codigoValido = codigoEsperado(idCliente).equals(codigoRecibido);
        boolean permitido = codigoValido && suscripcionService.tieneSuscripcionActiva(idCliente);
        Acceso acceso = new Acceso(
                idGenerator.generar("ACC"),
                idCliente, LocalDateTime.now(reloj), codigoRecibido,
                permitido ? "PERMITIDO" : "DENEGADO");
        return accesoDAO.guardar(acceso);
    }

    /** Convencion del QR simulado: CLI-ABC12345 se representa como QR-ABC12345. */
    static String codigoEsperado(String idCliente) {
        String sufijo = idCliente.startsWith("CLI-") ? idCliente.substring(4) : idCliente;
        return "QR-" + sufijo;
    }

    public List<Acceso> listar() {
        return accesoDAO.obtenerTodos();
    }
}
