package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.EntrenadorDAO;
import com.sv.gimnasio.model.Entrenador;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** DAO de entrenadores sobre PostgreSQL con JDBC (tabla entrenador). */
@Repository
public class EntrenadorDAOImpl extends BaseJdbcDAO implements EntrenadorDAO {

    private static final String SELECT_BASE =
            "SELECT id_entrenador, nombre, apellido, telefono, correo, especialidad, turno FROM entrenador";

    private static final String SQL_UPSERT =
            "INSERT INTO entrenador (id_entrenador, nombre, apellido, telefono, correo, especialidad, turno) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (id_entrenador) DO UPDATE SET "
                    + "nombre = EXCLUDED.nombre, apellido = EXCLUDED.apellido, "
                    + "telefono = EXCLUDED.telefono, correo = EXCLUDED.correo, "
                    + "especialidad = EXCLUDED.especialidad, turno = EXCLUDED.turno";

    public EntrenadorDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Entrenador> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY apellido, nombre";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Entrenador> entrenadores = new ArrayList<>();
            while (rs.next()) {
                entrenadores.add(mapear(rs));
            }
            return entrenadores;
        } catch (SQLException e) {
            throw error("listar entrenadores", e);
        }
    }

    @Override
    public Optional<Entrenador> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String sql = SELECT_BASE + " WHERE id_entrenador = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw error("consultar el entrenador " + id, e);
        }
    }

    @Override
    public Entrenador guardar(Entrenador entrenador) {
        if (entrenador == null || entrenador.getIdEntrenador() == null
                || entrenador.getIdEntrenador().isBlank()) {
            throw new IllegalArgumentException("El entrenador y su ID son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPSERT)) {
            ps.setString(1, entrenador.getIdEntrenador());
            ps.setString(2, entrenador.getNombre());
            ps.setString(3, entrenador.getApellido());
            ps.setString(4, entrenador.getTelefono());
            ps.setString(5, entrenador.getCorreo());
            ps.setString(6, entrenador.getEspecialidad());
            ps.setString(7, entrenador.getTurno());
            ps.executeUpdate();
            return entrenador;
        } catch (SQLException e) {
            throw error("guardar el entrenador " + entrenador.getIdEntrenador(), e);
        }
    }

    private Entrenador mapear(ResultSet rs) throws SQLException {
        return new Entrenador(
                rs.getString("id_entrenador"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("especialidad"),
                rs.getString("turno"));
    }
}
