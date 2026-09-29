package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.EntrenadorDAO;
import com.sv.gimnasio.model.Entrenador;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EntrenadorDAOImpl implements EntrenadorDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Entrenador> storage;
    private final Map<String, Entrenador> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/entrenadores.dat");
        storage.leerTodos().forEach(entrenador -> {
            if (entrenador.getIdEntrenador() != null && !entrenador.getIdEntrenador().isBlank()) {
                cache.put(entrenador.getIdEntrenador(), entrenador);
            }
        });
    }

    @Override
    public List<Entrenador> obtenerTodos() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Entrenador> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public Entrenador guardar(Entrenador entrenador) {
        if (entrenador == null || entrenador.getIdEntrenador() == null
                || entrenador.getIdEntrenador().isBlank()) {
            throw new IllegalArgumentException("El entrenador y su ID son obligatorios.");
        }
        cache.put(entrenador.getIdEntrenador(), entrenador);
        storage.guardarTodos(new ArrayList<>(cache.values()));
        return entrenador;
    }
}
