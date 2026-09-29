package com.sv.gimnasio.controller;

import com.sv.gimnasio.dto.AccesoRequestDTO;
import com.sv.gimnasio.model.Acceso;
import com.sv.gimnasio.service.AccesoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST API - Control de acceso QR simulado (HU-003). */
@RestController
@RequestMapping("/api/accesos")
@CrossOrigin(origins = "*")
public class AccesoController {

    private final AccesoService accesoService;

    public AccesoController(AccesoService accesoService) {
        this.accesoService = accesoService;
    }

    @PostMapping
    public Acceso registrar(@Valid @RequestBody AccesoRequestDTO dto) {
        return accesoService.registrarIntento(dto);
    }

    @GetMapping
    public List<Acceso> listar() {
        return accesoService.listar();
    }
}
