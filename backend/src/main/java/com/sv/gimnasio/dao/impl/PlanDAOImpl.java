package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.PlanDAO;
import com.sv.gimnasio.model.Plan;
import com.sv.gimnasio.model.PlanAnual;
import com.sv.gimnasio.model.PlanMensual;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PlanDAOImpl implements PlanDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Plan> storage;
    private final Map<String, Plan> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/planes.dat");
        storage.leerTodos().forEach(p -> cache.put(p.getIdPlan(), p));

        // Datos semilla: si no existen planes, se crean uno mensual y uno anual de ejemplo.
        if (cache.isEmpty()) {
            guardar(new PlanMensual("PLAN-MEN-01", 25.0));
            guardar(new PlanAnual("PLAN-ANU-01", 25.0));
        }
    }

    @Override
    public List<Plan> obtenerTodos() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Plan> obtenerPorId(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public Plan guardar(Plan plan) {
        cache.put(plan.getIdPlan(), plan);
        storage.guardarTodos(new ArrayList<>(cache.values()));
        return plan;
    }
}
