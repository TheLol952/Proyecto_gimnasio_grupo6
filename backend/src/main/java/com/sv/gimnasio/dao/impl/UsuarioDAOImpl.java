package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.UsuarioDAO;
import com.sv.gimnasio.model.Usuario;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UsuarioDAOImpl implements UsuarioDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Usuario> storage;
    private final Map<String, Usuario> cache = new HashMap<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/usuarios.dat");
        storage.leerTodos().forEach(u -> cache.put(u.getUsuario(), u));
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<Usuario> obtenerPorUsuario(String usuario) {
        return Optional.ofNullable(cache.get(usuario));
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        cache.put(usuario.getUsuario(), usuario);
        storage.guardarTodos(new ArrayList<>(cache.values()));
        return usuario;
    }
}
