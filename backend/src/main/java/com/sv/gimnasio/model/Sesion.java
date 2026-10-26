package com.sv.gimnasio.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idSesion;
    private String idCliente;
    private String idEntrenador;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    /** PROGRAMADA | CANCELADA | REALIZADA */
    private String estado;

    public Sesion() {
    }

    public Sesion(String idSesion, String idCliente, String idEntrenador,
                   LocalDateTime horaInicio, LocalDateTime horaFin, String estado) {
        this.idSesion = idSesion;
        this.idCliente = idCliente;
        this.idEntrenador = idEntrenador;
        setHoraInicio(horaInicio);
        setHoraFin(horaFin);
        this.estado = estado;
    }

    public String getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(String idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        if (horaInicio != null && horaFin != null && !horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        if (horaInicio != null && horaFin != null && !horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
