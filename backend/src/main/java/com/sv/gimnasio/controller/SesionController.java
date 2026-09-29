package com.sv.gimnasio.controller;

import com.sv.gimnasio.dto.SesionRequestDTO;
import com.sv.gimnasio.model.Sesion;
import com.sv.gimnasio.service.SesionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST API - Agenda de sesiones (HU-004 / HU-005). */
@RestController
@RequestMapping("/api/sesiones")
@CrossOrigin(origins = "*")
public class SesionController {

    private final SesionService sesionService;

    public SesionController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sesion agendar(@Valid @RequestBody SesionRequestDTO dto) {
        return sesionService.agendar(dto);
    }

    @GetMapping
    public List<Sesion> listar() {
        return sesionService.listarTodas();
    }

    @GetMapping("/entrenador/{idEntrenador}")
    public List<Sesion> listarPorEntrenador(@PathVariable String idEntrenador) {
        return sesionService.listarPorEntrenador(idEntrenador);
    }
}
