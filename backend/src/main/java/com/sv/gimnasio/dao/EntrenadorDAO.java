package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Entrenador;
import java.util.List;
import java.util.Optional;

public interface EntrenadorDAO {
    List<Entrenador> obtenerTodos();
    Optional<Entrenador> obtenerPorId(String id);
    Entrenador guardar(Entrenador entrenador);
}
