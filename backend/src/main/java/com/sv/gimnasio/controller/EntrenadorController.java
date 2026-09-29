package com.sv.gimnasio.controller;

import com.sv.gimnasio.dto.EntrenadorDTO;
import com.sv.gimnasio.model.Entrenador;
import com.sv.gimnasio.service.EntrenadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST API del módulo de entrenadores. */
@RestController
@RequestMapping("/api/entrenadores")
@CrossOrigin(origins = "*")
public class EntrenadorController {

    private final EntrenadorService entrenadorService;

    public EntrenadorController(EntrenadorService entrenadorService) {
        this.entrenadorService = entrenadorService;
    }

    @GetMapping
    public List<Entrenador> listar() {
        return entrenadorService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entrenador> obtener(@PathVariable String id) {
        return entrenadorService.obtener(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entrenador registrar(@Valid @RequestBody EntrenadorDTO dto) {
        return entrenadorService.registrar(dto);
    }

    @PutMapping("/{id}")
    public Entrenador actualizar(
            @PathVariable String id,
            @Valid @RequestBody EntrenadorDTO dto) {
        return entrenadorService.actualizar(id, dto);
    }
}
