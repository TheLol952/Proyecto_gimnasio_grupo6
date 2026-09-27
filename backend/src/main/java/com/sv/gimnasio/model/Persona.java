package com.sv.gimnasio.model;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Clase abstracta (HERENCIA) que agrupa los atributos y comportamiento comun
 * a Cliente y Entrenador. Encapsula sus atributos como privados y expone
 * getters/setters con validacion basica (ENCAPSULAMIENTO).
 */
public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Pattern CORREO_REGEX =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private String idPersona;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;

    protected Persona() {
    }

    protected Persona(String idPersona, String nombre, String apellido, String telefono, String correo) {
        this.idPersona = idPersona;
        setNombre(nombre);
        setApellido(apellido);
        setTelefono(telefono);
        setCorreo(correo);
    }

    public String getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar vacio.");
        }
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono != null && !telefono.isBlank() && !telefono.matches("[0-9+ -]{7,15}")) {
            throw new IllegalArgumentException("El telefono no tiene un formato valido.");
        }
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo != null && !correo.isBlank() && !CORREO_REGEX.matcher(correo).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato valido.");
        }
        this.correo = correo;
    }

    /**
     * POLIMORFISMO: cada subclase define como se describe a si misma.
     */
    public abstract String mostrarInformacion();

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
