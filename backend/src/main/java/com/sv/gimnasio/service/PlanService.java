package com.sv.gimnasio.service;

import com.sv.gimnasio.dao.PlanDAO;
import com.sv.gimnasio.model.Plan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    private final PlanDAO planDAO;

    public PlanService(PlanDAO planDAO) {
        this.planDAO = planDAO;
    }

    public List<Plan> listar() {
        return planDAO.obtenerTodos();
    }

    public Optional<Plan> obtener(String id) {
        return planDAO.obtenerPorId(id);
    }
}
