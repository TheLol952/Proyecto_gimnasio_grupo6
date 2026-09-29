package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.SesionDAO;
import com.sv.gimnasio.model.Sesion;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SesionDAOImpl implements SesionDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Sesion> storage;
    private final List<Sesion> sesiones = new ArrayList<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/sesiones.dat");
        sesiones.addAll(storage.leerTodos());
    }

    @Override
    public List<Sesion> obtenerTodos() {
        return new ArrayList<>(sesiones);
    }

    @Override
    public List<Sesion> obtenerPorEntrenador(String idEntrenador) {
        return sesiones.stream()
                .filter(s -> s.getIdEntrenador().equals(idEntrenador))
                .collect(Collectors.toList());
    }

    @Override
    public Sesion guardar(Sesion sesion) {
        sesiones.add(sesion);
        storage.guardarTodos(sesiones);
        return sesion;
    }
}
