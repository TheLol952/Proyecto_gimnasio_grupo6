package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.model.Suscripcion;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SuscripcionDAOImpl implements SuscripcionDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Suscripcion> storage;
    private final Map<String, Suscripcion> cache = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/suscripciones.dat");
        storage.leerTodos().forEach(s -> cache.put(s.getIdSuscripcion(), s));
    }

    @Override
    public List<Suscripcion> obtenerTodos() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Suscripcion> obtenerPorId(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Suscripcion> obtenerPorCliente(String idCliente) {
        return cache.values().stream()
                .filter(s -> idCliente.equals(s.getIdCliente()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized Suscripcion guardar(Suscripcion suscripcion) {
        cache.put(suscripcion.getIdSuscripcion(), suscripcion);
        storage.guardarTodos(new ArrayList<>(cache.values()));
        return suscripcion;
    }
}
