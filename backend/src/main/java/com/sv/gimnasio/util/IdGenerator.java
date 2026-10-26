package com.sv.gimnasio.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

/** Genera identificadores uniformes para las entidades del sistema. */
@Component
public class IdGenerator {

    public String generar(String prefijo) {
        if (prefijo == null || !prefijo.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("El prefijo del identificador debe contener tres letras.");
        }
        return prefijo.toUpperCase(Locale.ROOT) + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
