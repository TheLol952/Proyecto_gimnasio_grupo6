package com.sv.gimnasio.controller;

import com.sv.gimnasio.dto.ClienteDTO;
import com.sv.gimnasio.model.Cliente;
import com.sv.gimnasio.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock private ClienteService clienteService;

    @Test
    void devuelveNotFoundCuandoElClienteNoExiste() {
        when(clienteService.obtener("CLI-X")).thenReturn(Optional.empty());
        ClienteController controller = new ClienteController(clienteService);

        ResponseEntity<Cliente> respuesta = controller.obtener("CLI-X");

        assertEquals(404, respuesta.getStatusCode().value());
    }

    @Test
    void actualizaUnClienteExistente() {
        Cliente existente = new Cliente("CLI-1", "Ana", "Lopez", null, null, LocalDate.now());
        Cliente actualizado = new Cliente("CLI-1", "Ana Maria", "Lopez", null, null, LocalDate.now());
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Ana Maria");
        dto.setApellido("Lopez");
        when(clienteService.obtener("CLI-1")).thenReturn(Optional.of(existente));
        when(clienteService.actualizar("CLI-1", dto)).thenReturn(actualizado);
        ClienteController controller = new ClienteController(clienteService);

        ResponseEntity<Cliente> respuesta = controller.actualizar("CLI-1", dto);

        assertEquals(200, respuesta.getStatusCode().value());
        assertEquals("Ana Maria", respuesta.getBody().getNombre());
    }
}
