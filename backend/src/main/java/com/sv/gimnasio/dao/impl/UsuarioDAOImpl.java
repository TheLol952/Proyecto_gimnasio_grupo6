package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.UsuarioDAO;
import com.sv.gimnasio.model.Rol;
import com.sv.gimnasio.model.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO de usuarios sobre PostgreSQL con JDBC (tabla usuario).
 * Como en la version con .dat, el usuario se identifica por su nombre de
 * usuario: guardar un nombre que ya existe actualiza contrasena, rol y estado.
 */
@Repository
public class UsuarioDAOImpl extends BaseJdbcDAO implements UsuarioDAO {

    private static final String SELECT_BASE =
            "SELECT id_usuario, usuario, contrasena, rol, estado FROM usuario";

    private static final String SQL_UPSERT =
            "INSERT INTO usuario (id_usuario, usuario, contrasena, rol, estado) "
                    + "VALUES (?, ?, ?, ?, ?) "
                    + "ON CONFLICT (usuario) DO UPDATE SET "
                    + "contrasena = EXCLUDED.contrasena, rol = EXCLUDED.rol, estado = EXCLUDED.estado";

    public UsuarioDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Usuario> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY usuario";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return leerLista(rs, this::mapear);
        } catch (SQLException e) {
            throw error("listar usuarios", e);
        }
    }

    @Override
    public Optional<Usuario> obtenerPorUsuario(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            return Optional.empty();
        }
        String sql = SELECT_BASE + " WHERE usuario = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw error("consultar el usuario " + usuario, e);
        }
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario == null || usuario.getUsuario() == null || usuario.getUsuario().isBlank()
                || usuario.getIdUsuario() == null || usuario.getIdUsuario().isBlank()
                || usuario.getRol() == null) {
            throw new IllegalArgumentException("El usuario, su ID y su rol son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPSERT)) {
            ps.setString(1, usuario.getIdUsuario());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getContrasena());
            ps.setString(4, usuario.getRol().name());
            ps.setString(5, usuario.getEstado());
            ps.executeUpdate();
            return usuario;
        } catch (SQLException e) {
            throw error("guardar el usuario " + usuario.getUsuario(), e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getString("id_usuario"),
                rs.getString("usuario"),
                rs.getString("contrasena"),
                Rol.valueOf(rs.getString("rol")),
                rs.getString("estado"));
    }
}
