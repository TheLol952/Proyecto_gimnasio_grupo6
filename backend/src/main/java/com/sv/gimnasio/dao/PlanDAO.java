package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Plan;
import java.util.List;
import java.util.Optional;

public interface PlanDAO {
    List<Plan> obtenerTodos();
    Optional<Plan> obtenerPorId(String id);
    Plan guardar(Plan plan);
}
