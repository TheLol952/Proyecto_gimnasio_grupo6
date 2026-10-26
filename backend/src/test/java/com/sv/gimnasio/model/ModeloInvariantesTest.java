package com.sv.gimnasio.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ModeloInvariantesTest {

    @Test
    void suscripcionRechazaFechaFinAnterior() {
        assertThrows(IllegalArgumentException.class, () -> new Suscripcion(
                "SUS-1", "CLI-1", "PLAN-1",
                LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 24), "ACTIVA"));
    }

    @Test
    void sesionRechazaHoraFinAnterior() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 26, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> new Sesion(
                "SES-1", "CLI-1", "ENT-1", inicio, inicio.minusHours(1), "PROGRAMADA"));
    }
}
