package com.sv.gimnasio.controller;

import com.sv.gimnasio.dto.SuscripcionRequestDTO;
import com.sv.gimnasio.model.Suscripcion;
import com.sv.gimnasio.service.SuscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST API - Suscripcion (HU-002). */
@RestController
@RequestMapping("/api/suscripciones")
@CrossOrigin(origins = "*")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Suscripcion vender(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return suscripcionService.venderPlan(dto);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<Suscripcion> listarPorCliente(@PathVariable String idCliente) {
        return suscripcionService.listarPorCliente(idCliente);
    }
}
