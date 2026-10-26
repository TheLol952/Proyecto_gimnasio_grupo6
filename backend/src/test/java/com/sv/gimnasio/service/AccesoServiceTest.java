package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.AccesoDAO;
import com.sv.gimnasio.dto.AccesoRequestDTO;
import com.sv.gimnasio.model.Acceso;
import com.sv.gimnasio.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    @Mock private AccesoDAO accesoDAO;
    @Mock private SuscripcionService suscripcionService;
    @Mock private IdGenerator idGenerator;

    private AccesoService service;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-25T16:00:00Z"),
                ZoneId.of("America/El_Salvador"));
        service = new AccesoService(accesoDAO, suscripcionService, reloj, idGenerator);
        lenient().when(idGenerator.generar("ACC")).thenReturn("ACC-TEST0001");
        lenient().when(accesoDAO.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void permiteQrCorrectoConSuscripcionActiva() {
        when(suscripcionService.tieneSuscripcionActiva("CLI-ABC12345")).thenReturn(true);

        Acceso acceso = service.registrarIntento(dto("CLI-ABC12345", "QR-ABC12345"));

        assertEquals("PERMITIDO", acceso.getEstado());
        assertEquals("ACC-TEST0001", acceso.getIdAcceso());
    }

    @Test
    void deniegaQrQueNoCorrespondeAlCliente() {
        Acceso acceso = service.registrarIntento(dto("CLI-ABC12345", "QR-OTRO0001"));

        assertEquals("DENEGADO", acceso.getEstado());
        verifyNoInteractions(suscripcionService);
    }

    private AccesoRequestDTO dto(String cliente, String qr) {
        AccesoRequestDTO dto = new AccesoRequestDTO();
        dto.setIdCliente(cliente);
        dto.setCodigoQR(qr);
        return dto;
    }
}
