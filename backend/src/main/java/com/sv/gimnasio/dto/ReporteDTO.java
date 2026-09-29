package com.sv.gimnasio.dto;

/** DTO de salida con el resumen basico de la operacion del gimnasio (HU-006). */
public class ReporteDTO {

    private long totalClientes;
    private long suscripcionesActivas;
    private long suscripcionesVencidas;
    private double ingresosTotales;

    public ReporteDTO(long totalClientes, long suscripcionesActivas, long suscripcionesVencidas, double ingresosTotales) {
        this.totalClientes = totalClientes;
        this.suscripcionesActivas = suscripcionesActivas;
        this.suscripcionesVencidas = suscripcionesVencidas;
        this.ingresosTotales = ingresosTotales;
    }

    public long getTotalClientes() {
        return totalClientes;
    }

    public long getSuscripcionesActivas() {
        return suscripcionesActivas;
    }

    public long getSuscripcionesVencidas() {
        return suscripcionesVencidas;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }
}
