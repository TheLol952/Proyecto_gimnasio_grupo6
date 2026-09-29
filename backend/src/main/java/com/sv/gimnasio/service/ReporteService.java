package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.dao.PagoDAO;
import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.dto.ReporteDTO;
import org.springframework.stereotype.Service;

/** HU-006: reportes basicos de operacion del gimnasio. */
@Service
public class ReporteService {

    private final ClienteDAO clienteDAO;
    private final SuscripcionDAO suscripcionDAO;
    private final PagoDAO pagoDAO;

    public ReporteService(ClienteDAO clienteDAO, SuscripcionDAO suscripcionDAO, PagoDAO pagoDAO) {
        this.clienteDAO = clienteDAO;
        this.suscripcionDAO = suscripcionDAO;
        this.pagoDAO = pagoDAO;
    }

    public ReporteDTO generarReporte() {
        long totalClientes = clienteDAO.obtenerTodos().size();
        long activas = suscripcionDAO.obtenerTodos().stream()
                .filter(s -> "ACTIVA".equals(s.getEstado())).count();
        long vencidas = suscripcionDAO.obtenerTodos().stream()
                .filter(s -> "VENCIDA".equals(s.getEstado())).count();
        double ingresos = pagoDAO.obtenerTodos().stream().mapToDouble(p -> p.getMonto()).sum();
        return new ReporteDTO(totalClientes, activas, vencidas, ingresos);
    }
}
