package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dao.EntrenadorDAO;
import com.sv.gimnasio.dao.SesionDAO;
import com.sv.gimnasio.dto.HorarioDisponibleDTO;
import com.sv.gimnasio.dto.SesionRequestDTO;
import com.sv.gimnasio.model.Cliente;
import com.sv.gimnasio.model.Entrenador;
import com.sv.gimnasio.model.Sesion;
import com.sv.gimnasio.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesionServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 25, 10, 0);

    @Mock private SesionDAO sesionDAO;
    @Mock private ClienteDAO clienteDAO;
    @Mock private EntrenadorDAO entrenadorDAO;
    @Mock private IdGenerator idGenerator;

    private SesionService service;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(AHORA.atZone(ZONA).toInstant(), ZONA);
        service = new SesionService(sesionDAO, clienteDAO, entrenadorDAO, reloj, idGenerator);
    }

    @Test
    void rechazaUnaSesionEnElPasado() {
        SesionRequestDTO dto = dto(AHORA.minusHours(2), AHORA.minusHours(1));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> service.agendar(dto));

        assertTrue(error.getMessage().contains("posterior"));
        verifyNoInteractions(clienteDAO, entrenadorDAO, sesionDAO);
    }

    @Test
    void rechazaTraslapeConOtraSesion() {
        SesionRequestDTO dto = dto(AHORA.plusHours(2), AHORA.plusHours(3));
        when(clienteDAO.obtenerPorId("CLI-1")).thenReturn(Optional.of(cliente()));
        when(entrenadorDAO.obtenerPorId("ENT-1")).thenReturn(Optional.of(entrenador("MATUTINO")));
        when(sesionDAO.obtenerPorEntrenador("ENT-1")).thenReturn(List.of(
                new Sesion("SES-X", "CLI-X", "ENT-1",
                        AHORA.plusHours(1), AHORA.plusHours(2).plusMinutes(30), "PROGRAMADA")));

        assertThrows(IllegalStateException.class, () -> service.agendar(dto));
        verify(sesionDAO, never()).guardar(any());
    }

    @Test
    void agendaUnaSesionFuturaValida() {
        SesionRequestDTO dto = dto(AHORA.plusHours(2), AHORA.plusHours(3));
        when(clienteDAO.obtenerPorId("CLI-1")).thenReturn(Optional.of(cliente()));
        when(entrenadorDAO.obtenerPorId("ENT-1")).thenReturn(Optional.of(entrenador("MATUTINO")));
        when(sesionDAO.obtenerPorEntrenador("ENT-1")).thenReturn(List.of());
        when(idGenerator.generar("SES")).thenReturn("SES-TEST0001");
        when(sesionDAO.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        Sesion creada = service.agendar(dto);

        assertEquals("SES-TEST0001", creada.getIdSesion());
        assertEquals("PROGRAMADA", creada.getEstado());
        verify(sesionDAO).guardar(creada);
    }

    @Test
    void calculaDisponibilidadYExcluyeFranjasOcupadas() {
        LocalDate fecha = AHORA.toLocalDate().plusDays(1);
        when(entrenadorDAO.obtenerPorId("ENT-1")).thenReturn(Optional.of(entrenador("MATUTINO")));
        when(sesionDAO.obtenerPorEntrenador("ENT-1")).thenReturn(List.of(
                new Sesion("SES-X", "CLI-X", "ENT-1",
                        fecha.atTime(8, 0), fecha.atTime(9, 0), "PROGRAMADA")));

        List<HorarioDisponibleDTO> horarios = service.consultarDisponibilidad("ENT-1", fecha);

        assertEquals(5, horarios.size());
        assertFalse(horarios.stream().anyMatch(h -> h.getHoraInicio().equals(fecha.atTime(8, 0))));
        assertEquals(fecha.atTime(6, 0), horarios.getFirst().getHoraInicio());
    }

    private SesionRequestDTO dto(LocalDateTime inicio, LocalDateTime fin) {
        SesionRequestDTO dto = new SesionRequestDTO();
        dto.setIdCliente("CLI-1");
        dto.setIdEntrenador("ENT-1");
        dto.setHoraInicio(inicio);
        dto.setHoraFin(fin);
        return dto;
    }

    private Cliente cliente() {
        return new Cliente("CLI-1", "Ana", "Lopez", null, null, LocalDate.now());
    }

    private Entrenador entrenador(String turno) {
        return new Entrenador("ENT-1", "Luis", "Perez", null, null, "Fuerza", turno);
    }
}
