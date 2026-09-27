package com.sv.gimnasio.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Clase abstracta (HERENCIA / POLIMORFISMO). Cada tipo de plan sabe calcular
 * su propia fecha de fin y su propio precio final.
 */
public abstract class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idPlan;
    private String nombre;
    private double precioBase;

    protected Plan() {
    }

    protected Plan(String idPlan, String nombre, double precioBase) {
        this.idPlan = idPlan;
        this.nombre = nombre;
        setPrecioBase(precioBase);
    }

    public String getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(String idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo.");
        }
        this.precioBase = precioBase;
    }

    /** POLIMORFISMO: cada subclase define su propia vigencia. */
    public abstract LocalDate calcularFechaFin(LocalDate fechaInicio);

    /** POLIMORFISMO: cada subclase define su propio calculo de precio. */
    public abstract double precioFinal();

    @Override
    public String toString() {
        return nombre + " (" + getClass().getSimpleName() + ") - $" + String.format("%.2f", precioFinal());
    }
}
