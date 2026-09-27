package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.model.Cliente;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del DAO de clientes.
 *
 * Usa un HashMap como colección en memoria para búsquedas rápidas por ID y
 * DatFileStorage para persistir la información en clientes.dat.
 */
@Repository
public class ClienteDAOImpl implements ClienteDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Cliente> storage;

    private final Map<String, Cliente> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/clientes.dat");
        cargarDesdeArchivo();
    }

    private void cargarDesdeArchivo() {
        cache.clear();
        storage.leerTodos().forEach(cliente -> {
            if (cliente.getIdCliente() != null && !cliente.getIdCliente().isBlank()) {
                cache.put(cliente.getIdCliente(), cliente);
            }
        });
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Cliente> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null || cliente.getIdCliente().isBlank()) {
            throw new IllegalArgumentException("El cliente y su ID son obligatorios.");
        }

        cache.put(cliente.getIdCliente(), cliente);
        persistir();
        return cliente;
    }

    @Override
    public void eliminar(String id) {
        if (id != null && cache.remove(id) != null) {
            persistir();
        }
    }

    private void persistir() {
        List<Cliente> clientes = new ArrayList<>(cache.values());
        storage.guardarTodos(clientes);
    }
}
