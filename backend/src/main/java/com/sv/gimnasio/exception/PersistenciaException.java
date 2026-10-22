package com.sv.gimnasio.exception;

/**
 * Error de acceso a datos (JDBC). Es unchecked para no alterar las firmas de
 * las interfaces DAO existentes. Conserva la causa tecnica (SQLException) para
 * el log; el mensaje que ve el usuario lo define ApiExceptionHandler.
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** true si el fallo parece temporal (conexion caida, pool agotado, etc.). */
    private final boolean temporal;

    public PersistenciaException(String mensaje, Throwable causa, boolean temporal) {
        super(mensaje, causa);
        this.temporal = temporal;
    }

    public boolean isTemporal() {
        return temporal;
    }
}
