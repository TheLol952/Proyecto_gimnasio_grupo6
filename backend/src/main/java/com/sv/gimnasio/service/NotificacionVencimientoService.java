package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.model.Suscripcion;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * PROGRAMACION CONCURRENTE (HU-007).
 *
 * Justificacion tecnica: el estado de una suscripcion (ACTIVA/VENCIDA) es
 * consultado constantemente por el control de acceso QR (HU-003). Si esa
 * actualizacion dependiera unicamente de que un recepcionista revise
 * manualmente cada suscripcion, el sistema podria seguir marcando como
 * "activa" a un cliente cuya membresia ya vencio, o el administrador se
 * enteraria demasiado tarde de los vencimientos proximos.
 *
 * Por ello se ejecuta un HILO independiente en segundo plano que, de forma
 * periodica y sin bloquear el hilo principal de la aplicacion (ni las
 * peticiones REST atendidas por Spring), recorre la coleccion de
 * suscripciones para:
 *   1) Marcar como VENCIDA cualquier suscripcion cuya fecha de fin ya paso.
 *   2) Encolar (Queue) las suscripciones que vencen en los proximos 3 dias,
 *      para que el administrador pueda notificarlas (HU-007).
 */
@Service
public class NotificacionVencimientoService {

    private static final long INTERVALO_MS = 60_000; // cada 1 minuto (demo); en produccion podria ser cada hora.
    private static final int DIAS_AVISO_PREVIO = 3;

    private final SuscripcionDAO suscripcionDAO;

    /** Coleccion concurrente: el hilo de fondo escribe, los controladores REST leen. */
    private final Queue<Suscripcion> pendientesDeNotificar = new ConcurrentLinkedQueue<>();

    private Thread hiloVerificador;
    private volatile boolean activo = true;

    public NotificacionVencimientoService(SuscripcionDAO suscripcionDAO) {
        this.suscripcionDAO = suscripcionDAO;
    }

    @PostConstruct
    public void iniciar() {
        hiloVerificador = new Thread(this::verificarVencimientosPeriodicamente, "hilo-verificacion-vencimientos");
        hiloVerificador.setDaemon(true);
        hiloVerificador.start();
    }

    private void verificarVencimientosPeriodicamente() {
        while (activo) {
            try {
                verificarUnaVez();
            } catch (Exception e) {
                System.err.println("[hilo-verificacion-vencimientos] Error: " + e.getMessage());
            }
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                activo = false;
            }
        }
    }

    void verificarUnaVez() {
        LocalDate hoy = LocalDate.now();
        List<Suscripcion> suscripciones = suscripcionDAO.obtenerTodos();

        for (Suscripcion s : suscripciones) {
            if (s.getFechaFin() == null) {
                continue;
            }
            if (s.getFechaFin().isBefore(hoy) && !"VENCIDA".equals(s.getEstado())) {
                s.setEstado("VENCIDA");
                suscripcionDAO.guardar(s);
            } else if (!s.getFechaFin().isBefore(hoy)
                    && !s.getFechaFin().isAfter(hoy.plusDays(DIAS_AVISO_PREVIO))) {
                pendientesDeNotificar.offer(s);
            }
        }
    }

    /** Consultado por ReporteController/AdministradorController para mostrar avisos pendientes. */
    public List<Suscripcion> obtenerNotificacionesPendientes() {
        return new ArrayList<>(pendientesDeNotificar);
    }

    @PreDestroy
    public void detener() {
        activo = false;
        if (hiloVerificador != null) {
            hiloVerificador.interrupt();
        }
    }
}
