package com.sv.gimnasio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilidadesTest {

    @Test
    void generaIdConPrefijoNormalizado() {
        String id = new IdGenerator().generar("cli");

        assertTrue(id.matches("CLI-[0-9A-F]{8}"));
    }

    @Test
    void rechazaPrefijoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new IdGenerator().generar("CLIENTE"));
    }

    @Test
    void normalizaTextoOpcional() {
        assertEquals("texto", TextoUtils.normalizarOpcional("  texto  "));
        assertNull(TextoUtils.normalizarOpcional("   "));
        assertNull(TextoUtils.normalizarOpcional(null));
    }
}
