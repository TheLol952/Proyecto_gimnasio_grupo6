package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Sesion;
import java.util.List;

public interface SesionDAO {
    List<Sesion> obtenerTodos();
    List<Sesion> obtenerPorEntrenador(String idEntrenador);
    Sesion guardar(Sesion sesion);
}
