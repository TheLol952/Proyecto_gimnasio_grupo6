package com.sv.gimnasio.controller;

import com.sv.gimnasio.dto.ReporteDTO;
import com.sv.gimnasio.model.Suscripcion;
import com.sv.gimnasio.service.NotificacionVencimientoService;
import com.sv.gimnasio.service.ReporteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** REST API - Reportes basicos (HU-006) y notificaciones de vencimiento (HU-007). */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;
    private final NotificacionVencimientoService notificacionVencimientoService;

    public ReporteController(ReporteService reporteService,
                              NotificacionVencimientoService notificacionVencimientoService) {
        this.reporteService = reporteService;
        this.notificacionVencimientoService = notificacionVencimientoService;
    }

    @GetMapping
    public ReporteDTO generar() {
        return reporteService.generarReporte();
    }

    @GetMapping("/vencimientos-proximos")
    public List<Suscripcion> vencimientosProximos() {
        return notificacionVencimientoService.obtenerNotificacionesPendientes();
    }
}
