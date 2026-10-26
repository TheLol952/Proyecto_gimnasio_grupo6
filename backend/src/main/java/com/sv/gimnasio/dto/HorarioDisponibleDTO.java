package com.sv.gimnasio.dto;

import java.time.LocalDateTime;

/** Franja de una hora que puede reservarse con un entrenador. */
public class HorarioDisponibleDTO {

    private final LocalDateTime horaInicio;
    private final LocalDateTime horaFin;

    public HorarioDisponibleDTO(LocalDateTime horaInicio, LocalDateTime horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }
}
