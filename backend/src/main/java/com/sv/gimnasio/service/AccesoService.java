package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.AccesoDAO;
import com.sv.gimnasio.dto.AccesoRequestDTO;
import com.sv.gimnasio.model.Acceso;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** HU-003: control de acceso mediante codigo QR simulado. */
@Service
public class AccesoService {

    private final AccesoDAO accesoDAO;
    private final SuscripcionService suscripcionService;

    public AccesoService(AccesoDAO accesoDAO, SuscripcionService suscripcionService) {
        this.accesoDAO = accesoDAO;
        this.suscripcionService = suscripcionService;
    }

    public Acceso registrarIntento(AccesoRequestDTO dto) {
        boolean permitido = suscripcionService.tieneSuscripcionActiva(dto.getIdCliente());
        Acceso acceso = new Acceso(
                "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                dto.getIdCliente(), LocalDateTime.now(), dto.getCodigoQR(),
                permitido ? "PERMITIDO" : "DENEGADO");
        return accesoDAO.guardar(acceso);
    }

    public List<Acceso> listar() {
        return accesoDAO.obtenerTodos();
    }
}
