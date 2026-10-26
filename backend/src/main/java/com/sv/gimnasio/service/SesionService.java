package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dao.EntrenadorDAO;
import com.sv.gimnasio.dao.SesionDAO;
import com.sv.gimnasio.dto.HorarioDisponibleDTO;
import com.sv.gimnasio.dto.SesionRequestDTO;
import com.sv.gimnasio.model.Entrenador;
import com.sv.gimnasio.model.Sesion;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final ClienteDAO clienteDAO;
    private final EntrenadorDAO entrenadorDAO;
    private final Clock reloj;

    public SesionService(SesionDAO sesionDAO, ClienteDAO clienteDAO,
                         EntrenadorDAO entrenadorDAO, Clock reloj) {
        this.sesionDAO = sesionDAO;
        this.clienteDAO = clienteDAO;
        this.entrenadorDAO = entrenadorDAO;
        this.reloj = reloj;
    }

    public Sesion agendar(SesionRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la sesion son obligatorios.");
        }
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new IllegalArgumentException("Las horas de inicio y fin son obligatorias.");
        }
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (!dto.getHoraInicio().isAfter(ahora)) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser posterior a la fecha y hora actuales.");
        }
        if (dto.getHoraFin().isBefore(dto.getHoraInicio())
                || dto.getHoraFin().isEqual(dto.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        if (clienteDAO.obtenerPorId(dto.getIdCliente()).isEmpty()) {
            throw new IllegalArgumentException("El cliente seleccionado no existe.");
        }
        if (entrenadorDAO.obtenerPorId(dto.getIdEntrenador()).isEmpty()) {
            throw new IllegalArgumentException("El entrenador seleccionado no existe.");
        }

        TreeMap<LocalDateTime, Sesion> agendaEntrenador = new TreeMap<>();
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

    /**
     * HU-005: calcula franjas de una hora dentro del turno del entrenador y
     * excluye las sesiones programadas que se traslapan.
     */
    public List<HorarioDisponibleDTO> consultarDisponibilidad(String idEntrenador, LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de consulta es obligatoria.");
        }
        LocalDate hoy = LocalDate.now(reloj);
        if (fecha.isBefore(hoy)) {
            throw new IllegalArgumentException("No se puede consultar disponibilidad en una fecha pasada.");
        }

        Entrenador entrenador = entrenadorDAO.obtenerPorId(idEntrenador)
                .orElseThrow(() -> new NoSuchElementException(
                        "Entrenador no encontrado: " + idEntrenador));
        LocalTime[] limites = limitesTurno(entrenador.getTurno());
        LocalDateTime cursor = LocalDateTime.of(fecha, limites[0]);
        LocalDateTime finTurno = LocalDateTime.of(fecha, limites[1]);
        LocalDateTime ahora = LocalDateTime.now(reloj);
        List<Sesion> agenda = sesionDAO.obtenerPorEntrenador(idEntrenador).stream()
                .filter(sesion -> !"CANCELADA".equals(sesion.getEstado()))
                .toList();
        List<HorarioDisponibleDTO> disponibles = new ArrayList<>();

        while (!cursor.plusHours(1).isAfter(finTurno)) {
            LocalDateTime inicioFranja = cursor;
            LocalDateTime finFranja = cursor.plusHours(1);
            boolean futura = inicioFranja.isAfter(ahora);
            boolean ocupada = agenda.stream().anyMatch(sesion ->
                    inicioFranja.isBefore(sesion.getHoraFin())
                            && finFranja.isAfter(sesion.getHoraInicio()));
            if (futura && !ocupada) {
                disponibles.add(new HorarioDisponibleDTO(inicioFranja, finFranja));
            }
            cursor = finFranja;
        }
        return disponibles;
    }

    private LocalTime[] limitesTurno(String turno) {
        return switch (turno == null ? "" : turno.toUpperCase()) {
            case "MATUTINO" -> new LocalTime[]{LocalTime.of(6, 0), LocalTime.of(12, 0)};
            case "VESPERTINO" -> new LocalTime[]{LocalTime.of(12, 0), LocalTime.of(18, 0)};
            case "NOCTURNO" -> new LocalTime[]{LocalTime.of(18, 0), LocalTime.of(22, 0)};
            case "MIXTO" -> new LocalTime[]{LocalTime.of(6, 0), LocalTime.of(22, 0)};
            default -> throw new IllegalStateException("El entrenador no tiene un turno valido.");
        };
    }
}
