package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dao.PagoDAO;
import com.sv.gimnasio.dao.PlanDAO;
import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.dto.SuscripcionRequestDTO;
import com.sv.gimnasio.model.Cliente;
import com.sv.gimnasio.model.Pago;
import com.sv.gimnasio.model.PlanMensual;
import com.sv.gimnasio.model.Suscripcion;
import com.sv.gimnasio.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionServiceTest {

    @Mock private SuscripcionDAO suscripcionDAO;
    @Mock private PlanDAO planDAO;
    @Mock private PagoDAO pagoDAO;
    @Mock private ClienteDAO clienteDAO;
    @Mock private IdGenerator idGenerator;

    private SuscripcionService service;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-25T16:00:00Z"),
                ZoneId.of("America/El_Salvador"));
        service = new SuscripcionService(
                suscripcionDAO, planDAO, pagoDAO, clienteDAO, reloj, idGenerator);
    }

    @Test
    void guardaSuscripcionYPagoEnOrden() {
        SuscripcionRequestDTO dto = new SuscripcionRequestDTO();
        dto.setIdCliente("CLI-1");
        dto.setIdPlan("PLAN-1");
        dto.setMetodoPago("EFECTIVO");
        when(clienteDAO.obtenerPorId("CLI-1")).thenReturn(Optional.of(
                new Cliente("CLI-1", "Ana", "Lopez", null, null, LocalDate.now())));
        when(planDAO.obtenerPorId("PLAN-1")).thenReturn(Optional.of(new PlanMensual("PLAN-1", 25)));
        when(idGenerator.generar("SUS")).thenReturn("SUS-TEST0001");
        when(idGenerator.generar("PAG")).thenReturn("PAG-TEST0001");

        Suscripcion resultado = service.venderPlan(dto);

        assertEquals(LocalDate.of(2026, 10, 25), resultado.getFechaInicio());
        assertEquals(LocalDate.of(2026, 11, 25), resultado.getFechaFin());
        InOrder orden = inOrder(suscripcionDAO, pagoDAO);
        orden.verify(suscripcionDAO).guardar(any(Suscripcion.class));
        orden.verify(pagoDAO).guardar(any(Pago.class));
    }

    @Test
    void consultaLaBaseParaDeterminarVigencia() {
        when(suscripcionDAO.obtenerPorCliente("CLI-1")).thenReturn(List.of(
                new Suscripcion("SUS-1", "CLI-1", "PLAN-1",
                        LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), "ACTIVA")));

        assertTrue(service.tieneSuscripcionActiva("CLI-1"));
        verify(suscripcionDAO).obtenerPorCliente("CLI-1");
    }
}
