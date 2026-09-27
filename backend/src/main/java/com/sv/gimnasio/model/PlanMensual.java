package com.sv.gimnasio.model;

import java.time.LocalDate;

public class PlanMensual extends Plan {

    private static final long serialVersionUID = 1L;

    public PlanMensual() {
        super();
    }

    public PlanMensual(String idPlan, double precioBase) {
        super(idPlan, "Plan Mensual", precioBase);
    }

    @Override
    public LocalDate calcularFechaFin(LocalDate fechaInicio) {
        return fechaInicio.plusMonths(1);
    }

    @Override
    public double precioFinal() {
        return getPrecioBase();
    }
}
