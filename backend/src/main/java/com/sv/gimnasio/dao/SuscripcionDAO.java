package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Suscripcion;
import java.util.List;
import java.util.Optional;

public interface SuscripcionDAO {
    List<Suscripcion> obtenerTodos();
    Optional<Suscripcion> obtenerPorId(String id);
    List<Suscripcion> obtenerPorCliente(String idCliente);
    Suscripcion guardar(Suscripcion suscripcion);
}
