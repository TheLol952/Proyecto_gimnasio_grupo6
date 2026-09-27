package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    List<Usuario> obtenerTodos();
    Optional<Usuario> obtenerPorUsuario(String usuario);
    Usuario guardar(Usuario usuario);
}
