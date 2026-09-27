package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Cliente;

import java.util.List;
import java.util.Optional;

/** Acceso a datos del módulo de clientes. */
public interface ClienteDAO {

    List<Cliente> obtenerTodos();

    Optional<Cliente> obtenerPorId(String id);

    Cliente guardar(Cliente cliente);

    void eliminar(String id);
}
