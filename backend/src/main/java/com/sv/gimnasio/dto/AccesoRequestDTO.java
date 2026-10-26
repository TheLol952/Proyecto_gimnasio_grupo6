package com.sv.gimnasio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de entrada para simular la lectura de un codigo QR en el acceso. */
public class AccesoRequestDTO {

    @NotBlank(message = "El id del cliente es obligatorio")
    private String idCliente;

    @NotBlank(message = "El codigo QR es obligatorio")
    @Size(max = 100, message = "El codigo QR no puede superar 100 caracteres")
    private String codigoQR;

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(String codigoQR) {
        this.codigoQR = codigoQR;
    }
}
