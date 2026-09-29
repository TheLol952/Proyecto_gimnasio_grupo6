package com.sv.gimnasio.model;

/**
 * HERENCIA: Entrenador extiende de Persona.
 */
public class Entrenador extends Persona {

    private static final long serialVersionUID = 1L;

    private String especialidad;
    private String turno;

    public Entrenador() {
        super();
    }

    public Entrenador(String idEntrenador, String nombre, String apellido, String telefono,
                       String correo, String especialidad, String turno) {
        super(idEntrenador, nombre, apellido, telefono, correo);
        this.especialidad = especialidad;
        this.turno = turno;
    }

    public String getIdEntrenador() {
        return getIdPersona();
    }

    public void setIdEntrenador(String idEntrenador) {
        setIdPersona(idEntrenador);
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String mostrarInformacion() {
        return "Entrenador[" + getIdEntrenador() + "] " + getNombre() + " " + getApellido()
                + " - Especialidad: " + especialidad + " - Turno: " + turno;
    }
}
