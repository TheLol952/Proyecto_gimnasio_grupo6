package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.SesionDAO;
import com.sv.gimnasio.dto.SesionRequestDTO;
import com.sv.gimnasio.model.Sesion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

/**
 * HU-004/HU-005: agenda de sesiones cliente-entrenador. Usa una COLECCION
 * TreeMap (ordenada por hora de inicio) para poder detectar traslapes de
 * horario por entrenador de forma sencilla.
 */
@Service
public class SesionService {

    private final SesionDAO sesionDAO;

    public SesionService(SesionDAO sesionDAO) {
        this.sesionDAO = sesionDAO;
    }

    public Sesion agendar(SesionRequestDTO dto) {
        TreeMap<java.time.LocalDateTime, Sesion> agendaEntrenador = new TreeMap<>();
        for (Sesion s : sesionDAO.obtenerPorEntrenador(dto.getIdEntrenador())) {
            agendaEntrenador.put(s.getHoraInicio(), s);
        }

        boolean traslape = agendaEntrenador.values().stream().anyMatch(s ->
                dto.getHoraInicio().isBefore(s.getHoraFin()) && dto.getHoraFin().isAfter(s.getHoraInicio()));
        if (traslape) {
            throw new IllegalStateException("El entrenador ya tiene una sesion en ese horario.");
        }

        Sesion sesion = new Sesion("SES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                dto.getIdCliente(), dto.getIdEntrenador(), dto.getHoraInicio(), dto.getHoraFin(), "PROGRAMADA");
        return sesionDAO.guardar(sesion);
    }

    public List<Sesion> listarPorEntrenador(String idEntrenador) {
        return sesionDAO.obtenerPorEntrenador(idEntrenador);
    }

    public List<Sesion> listarTodas() {
        return sesionDAO.obtenerTodos();
    }
}
