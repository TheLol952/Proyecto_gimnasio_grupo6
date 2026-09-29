package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Acceso;
import java.util.List;

public interface AccesoDAO {
    List<Acceso> obtenerTodos();
    Acceso guardar(Acceso acceso);
}
