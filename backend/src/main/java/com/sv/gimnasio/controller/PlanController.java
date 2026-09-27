package com.sv.gimnasio.controller;

import com.sv.gimnasio.model.Plan;
import com.sv.gimnasio.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
@CrossOrigin(origins = "*")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public List<Plan> listar() {
        return planService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtener(@PathVariable String id) {
        return planService.obtener(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
