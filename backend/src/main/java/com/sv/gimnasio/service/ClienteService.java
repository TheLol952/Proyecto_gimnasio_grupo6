package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dto.ClienteDTO;
import com.sv.gimnasio.model.Cliente;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/** Lógica de negocio de la HU-001: registro y consulta de clientes. */
@Service
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listar() {
        return clienteDAO.obtenerTodos();
    }

    public Optional<Cliente> obtener(String id) {
        return clienteDAO.obtenerPorId(id);
    }

    /** Registra un cliente generando un identificador único. */
    public Cliente registrar(ClienteDTO dto) {
        validarDTO(dto);

        String id = "CLI-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        Cliente cliente = new Cliente(
                id,
                dto.getNombre().trim(),
                dto.getApellido().trim(),
                normalizar(dto.getTelefono()),
                normalizar(dto.getCorreo()),
                LocalDate.now()
        );

        return clienteDAO.guardar(cliente);
    }

    /** Actualiza los datos editables de un cliente existente. */
    public Cliente actualizar(String id, ClienteDTO dto) {
        validarDTO(dto);

        Cliente cliente = clienteDAO.obtenerPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado: " + id));

        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido().trim());
        cliente.setTelefono(normalizar(dto.getTelefono()));
        cliente.setCorreo(normalizar(dto.getCorreo()));

        return clienteDAO.guardar(cliente);
    }

    private void validarDTO(ClienteDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del cliente son obligatorios.");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String resultado = valor.trim();
        return resultado.isEmpty() ? null : resultado;
    }
}
