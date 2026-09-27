package com.sv.gimnasio.model;

import java.time.LocalDate;

/**
 * Entidad Cliente.
 * HERENCIA: extiende Persona y reutiliza los datos comunes de una persona.
 * POLIMORFISMO: implementa mostrarInformacion() de Persona.
 */
public class Cliente extends Persona {

    private static final long serialVersionUID = 1L;

    private LocalDate fechaRegistro;

    public Cliente() {
        super();
    }

    public Cliente(String idCliente, String nombre, String apellido, String telefono,
                    String correo, LocalDate fechaRegistro) {
        super(idCliente, nombre, apellido, telefono, correo);
        this.fechaRegistro = fechaRegistro == null ? LocalDate.now() : fechaRegistro;
    }

    public String getIdCliente() {
        return getIdPersona();
    }

    public void setIdCliente(String idCliente) {
        setIdPersona(idCliente);
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String mostrarInformacion() {
        return "Cliente[" + getIdCliente() + "] " + getNombre() + " " + getApellido()
                + " - registrado el " + fechaRegistro;
    }
}
