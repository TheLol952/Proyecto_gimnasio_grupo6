package com.sv.gimnasio.dto;

import jakarta.validation.constraints.NotBlank;

/** DTO de entrada para vender un plan/suscripcion a un cliente. */
public class SuscripcionRequestDTO {

    @NotBlank(message = "El id del cliente es obligatorio")
    private String idCliente;

    @NotBlank(message = "El id del plan es obligatorio")
    private String idPlan;

    private String metodoPago;

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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
