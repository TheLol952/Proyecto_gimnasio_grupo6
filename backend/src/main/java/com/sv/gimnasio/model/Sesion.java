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
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
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
        this.horaInicio = horaInicio;
    }

    public LocalDateTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalDateTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
