package com.sv.gimnasio.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Suscripcion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idSuscripcion;
    private String idCliente;
    private String idPlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    /** ACTIVA | VENCIDA */
    private String estado;

    public Suscripcion() {
    }

    public Suscripcion(String idSuscripcion, String idCliente, String idPlan,
                        LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.idSuscripcion = idSuscripcion;
        this.idCliente = idCliente;
        this.idPlan = idPlan;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public String getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(String idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Suscripcion[" + idSuscripcion + "] cliente=" + idCliente + " plan=" + idPlan
                + " vigencia=" + fechaInicio + " a " + fechaFin + " estado=" + estado;
    }
}
