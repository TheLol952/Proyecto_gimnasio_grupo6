package com.sv.gimnasio.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idPago;
    private String idSuscripcion;
    private LocalDateTime fecha;
    private double monto;
    private String metodoPago;

    public Pago() {
    }

    public Pago(String idPago, String idSuscripcion, LocalDateTime fecha, double monto, String metodoPago) {
        this.idPago = idPago;
        this.idSuscripcion = idSuscripcion;
        this.fecha = fecha;
        setMonto(monto);
        this.metodoPago = metodoPago;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(String idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        if (monto < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
