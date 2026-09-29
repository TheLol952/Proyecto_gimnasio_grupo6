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
        storage.leerTodos().forEach(e -> cache.put(e.getIdEntrenador(), e));
    }

    @Override
    public List<Entrenador> obtenerTodos() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Entrenador> obtenerPorId(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public Entrenador guardar(Entrenador entrenador) {
        cache.put(entrenador.getIdEntrenador(), entrenador);
        storage.guardarTodos(new ArrayList<>(cache.values()));
        return entrenador;
    }
}
