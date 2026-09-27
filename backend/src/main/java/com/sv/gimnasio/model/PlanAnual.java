package com.sv.gimnasio.model;

import java.time.LocalDate;

public class PlanAnual extends Plan {

    private static final long serialVersionUID = 1L;

    /** Descuento aplicado por pagar el plan anual completo (15% por defecto). */
    private double descuento = 0.15;

    public PlanAnual() {
        super();
    }

    public PlanAnual(String idPlan, double precioBase) {
        super(idPlan, "Plan Anual", precioBase);
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        if (descuento < 0 || descuento > 1) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 1.");
        }
        this.descuento = descuento;
    }

    @Override
    public LocalDate calcularFechaFin(LocalDate fechaInicio) {
        return fechaInicio.plusYears(1);
    }

    @Override
    public double precioFinal() {
        return getPrecioBase() * 12 * (1 - descuento);
    }
}
