package com.sv.gimnasio.util;

/** Operaciones pequenas y reutilizables para datos de entrada textuales. */
public final class TextoUtils {

    private TextoUtils() {
    }

    public static String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String resultado = valor.trim();
        return resultado.isEmpty() ? null : resultado;
    }
}
