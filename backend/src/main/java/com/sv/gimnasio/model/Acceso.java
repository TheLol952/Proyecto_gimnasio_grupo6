package com.sv.gimnasio.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Acceso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idAcceso;
    private String idCliente;
    private LocalDateTime fechaHora;
    private String codigoQR;
    /** PERMITIDO | DENEGADO */
    private String estado;

    public Acceso() {
    }

    public Acceso(String idAcceso, String idCliente, LocalDateTime fechaHora, String codigoQR, String estado) {
        this.idAcceso = idAcceso;
        this.idCliente = idCliente;
        this.fechaHora = fechaHora;
        this.codigoQR = codigoQR;
        this.estado = estado;
    }

    public String getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(String idAcceso) {
        this.idAcceso = idAcceso;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(String codigoQR) {
        this.codigoQR = codigoQR;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
