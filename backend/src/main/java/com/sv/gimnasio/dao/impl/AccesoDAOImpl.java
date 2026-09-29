package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.AccesoDAO;
import com.sv.gimnasio.model.Acceso;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AccesoDAOImpl implements AccesoDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Acceso> storage;
    private final List<Acceso> accesos = new ArrayList<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/accesos.dat");
        accesos.addAll(storage.leerTodos());
    }

    @Override
    public List<Acceso> obtenerTodos() {
        return new ArrayList<>(accesos);
    }

    @Override
    public Acceso guardar(Acceso acceso) {
        accesos.add(acceso);
        storage.guardarTodos(accesos);
        return acceso;
    }
}
