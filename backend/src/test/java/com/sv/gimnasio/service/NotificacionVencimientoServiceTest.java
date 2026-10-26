package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.model.Suscripcion;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificacionVencimientoServiceTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-10-25T16:00:00Z"),
            ZoneId.of("America/El_Salvador"));

    @Test
    void noDuplicaNotificacionesEntreCiclos() {
        SuscripcionDAO dao = mock(SuscripcionDAO.class);
        Suscripcion proxima = suscripcion("SUS-1", LocalDate.of(2026, 10, 27), "ACTIVA");
        when(dao.obtenerTodos()).thenReturn(List.of(proxima));
        NotificacionVencimientoService service = new NotificacionVencimientoService(dao, reloj);

        service.verificarUnaVez();
        service.verificarUnaVez();

        assertEquals(1, service.obtenerNotificacionesPendientes().size());
    }

    @Test
    void marcaComoVencidaUnaSuscripcionPasada() {
        SuscripcionDAO dao = mock(SuscripcionDAO.class);
        Suscripcion vencida = suscripcion("SUS-2", LocalDate.of(2026, 10, 24), "ACTIVA");
        when(dao.obtenerTodos()).thenReturn(List.of(vencida));
        NotificacionVencimientoService service = new NotificacionVencimientoService(dao, reloj);

        service.verificarUnaVez();

        assertEquals("VENCIDA", vencida.getEstado());
        verify(dao).guardar(vencida);
    }

    private Suscripcion suscripcion(String id, LocalDate fin, String estado) {
        return new Suscripcion(id, "CLI-1", "PLAN-1", fin.minusMonths(1), fin, estado);
    }
}
