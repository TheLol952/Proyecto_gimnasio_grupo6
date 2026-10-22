package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.EntrenadorDAO;
import com.sv.gimnasio.dto.EntrenadorDTO;
import com.sv.gimnasio.model.Entrenador;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/** Lógica de negocio para el registro y consulta de entrenadores. */
@Service
public class EntrenadorService {

    private final EntrenadorDAO entrenadorDAO;

    public EntrenadorService(EntrenadorDAO entrenadorDAO) {
        this.entrenadorDAO = entrenadorDAO;
    }

    public List<Entrenador> listar() {
        return entrenadorDAO.obtenerTodos().stream()
                .sorted(Comparator.comparing(Entrenador::getApellido)
                        .thenComparing(Entrenador::getNombre))
                .toList();
    }

    public Optional<Entrenador> obtener(String id) {
        return entrenadorDAO.obtenerPorId(id);
    }

    public Entrenador registrar(EntrenadorDTO dto) {
        validarDTO(dto);

        String id = "ENT-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        Entrenador entrenador = new Entrenador(
                id,
                dto.getNombre().trim(),
                dto.getApellido().trim(),
                normalizar(dto.getTelefono()),
                normalizar(dto.getCorreo()),
                dto.getEspecialidad().trim(),
                dto.getTurno().trim()
        );

        return entrenadorDAO.guardar(entrenador);
    }

    public Entrenador actualizar(String id, EntrenadorDTO dto) {
        validarDTO(dto);

        Entrenador entrenador = entrenadorDAO.obtenerPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Entrenador no encontrado: " + id));

        entrenador.setNombre(dto.getNombre().trim());
        entrenador.setApellido(dto.getApellido().trim());
        entrenador.setTelefono(normalizar(dto.getTelefono()));
        entrenador.setCorreo(normalizar(dto.getCorreo()));
        entrenador.setEspecialidad(dto.getEspecialidad().trim());
        entrenador.setTurno(dto.getTurno().trim());

        return entrenadorDAO.guardar(entrenador);
    }

    private void validarDTO(EntrenadorDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del entrenador son obligatorios.");
        }
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (dto.getEspecialidad() == null || dto.getEspecialidad().isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        if (dto.getTurno() == null || dto.getTurno().isBlank()) {
            throw new IllegalArgumentException("El turno es obligatorio.");
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
