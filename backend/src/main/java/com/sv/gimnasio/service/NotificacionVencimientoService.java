package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.model.Suscripcion;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
 * Por ello se ejecuta una tarea programada en segundo plano que, de forma
 * periodica y sin bloquear el hilo principal de la aplicacion (ni las
 * peticiones REST atendidas por Spring), recorre la coleccion de
 * suscripciones para:
 *   1) Marcar como VENCIDA cualquier suscripcion cuya fecha de fin ya paso.
 *   2) Encolar (Queue) las suscripciones que vencen en los proximos 3 dias,
 *      para que el administrador pueda notificarlas (HU-007).
 */
@Service
public class NotificacionVencimientoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionVencimientoService.class);
    private static final long INTERVALO_MS = 60_000; // cada 1 minuto (demo); en produccion podria ser cada hora.
    private static final int DIAS_AVISO_PREVIO = 3;

    private final SuscripcionDAO suscripcionDAO;
    private final Clock reloj;

    /**
     * Mapa concurrente que evita encolar la misma suscripcion en cada ciclo.
     * La tarea escribe mientras los controladores REST consultan una copia.
     */
    private final Map<String, Suscripcion> pendientesDeNotificar = new ConcurrentHashMap<>();

    private ScheduledExecutorService ejecutor;

    public NotificacionVencimientoService(SuscripcionDAO suscripcionDAO, Clock reloj) {
        this.suscripcionDAO = suscripcionDAO;
        this.reloj = reloj;
    }

    @PostConstruct
    public void iniciar() {
        ejecutor = Executors.newSingleThreadScheduledExecutor(tarea -> {
            Thread hilo = new Thread(tarea, "verificacion-vencimientos");
            hilo.setDaemon(true);
            return hilo;
        });
        ejecutor.scheduleWithFixedDelay(
                this::verificarConManejoDeErrores, 0, INTERVALO_MS, TimeUnit.MILLISECONDS);
    }

    private void verificarConManejoDeErrores() {
        try {
            verificarUnaVez();
        } catch (RuntimeException e) {
            log.error("No fue posible verificar los vencimientos de suscripciones", e);
        }
    }

    void verificarUnaVez() {
        LocalDate hoy = LocalDate.now(reloj);
        List<Suscripcion> suscripciones = suscripcionDAO.obtenerTodos();

        for (Suscripcion s : suscripciones) {
            if (s.getFechaFin() == null) {
                continue;
            }
            if (s.getFechaFin().isBefore(hoy) && !"VENCIDA".equals(s.getEstado())) {
                s.setEstado("VENCIDA");
                suscripcionDAO.guardar(s);
                pendientesDeNotificar.remove(s.getIdSuscripcion());
            } else if ("ACTIVA".equals(s.getEstado())
                    && !s.getFechaFin().isBefore(hoy)
                    && !s.getFechaFin().isAfter(hoy.plusDays(DIAS_AVISO_PREVIO))) {
                pendientesDeNotificar.put(s.getIdSuscripcion(), s);
            } else {
                pendientesDeNotificar.remove(s.getIdSuscripcion());
            }
        }
    }

    /** Consultado por ReporteController/AdministradorController para mostrar avisos pendientes. */
    public List<Suscripcion> obtenerNotificacionesPendientes() {
        List<Suscripcion> pendientes = new ArrayList<>(pendientesDeNotificar.values());
        pendientes.sort(Comparator.comparing(Suscripcion::getFechaFin)
                .thenComparing(Suscripcion::getIdSuscripcion));
        return pendientes;
    }

    @PreDestroy
    public void detener() {
        if (ejecutor == null) {
            return;
        }
        ejecutor.shutdownNow();
        try {
            if (!ejecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("El verificador de vencimientos no finalizo dentro del tiempo esperado");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
