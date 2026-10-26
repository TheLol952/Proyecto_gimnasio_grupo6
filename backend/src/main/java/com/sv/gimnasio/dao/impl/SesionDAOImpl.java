package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.SesionDAO;
import com.sv.gimnasio.model.Sesion;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** DAO de sesiones cliente-entrenador sobre PostgreSQL con JDBC (tabla sesion). */
@Repository
public class SesionDAOImpl extends BaseJdbcDAO implements SesionDAO {

    private static final String SELECT_BASE =
            "SELECT id_sesion, id_cliente, id_entrenador, hora_inicio, hora_fin, estado FROM sesion";

    private static final String SQL_UPSERT =
            "INSERT INTO sesion (id_sesion, id_cliente, id_entrenador, hora_inicio, hora_fin, estado) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (id_sesion) DO UPDATE SET "
                    + "id_cliente = EXCLUDED.id_cliente, id_entrenador = EXCLUDED.id_entrenador, "
                    + "hora_inicio = EXCLUDED.hora_inicio, hora_fin = EXCLUDED.hora_fin, "
                    + "estado = EXCLUDED.estado";

    public SesionDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Sesion> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY hora_inicio, id_sesion";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return leerLista(rs, this::mapear);
        } catch (SQLException e) {
            throw error("listar sesiones", e);
        }
    }

    @Override
    public List<Sesion> obtenerPorEntrenador(String idEntrenador) {
        String sql = SELECT_BASE + " WHERE id_entrenador = ? ORDER BY hora_inicio, id_sesion";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idEntrenador);
            try (ResultSet rs = ps.executeQuery()) {
                return leerLista(rs, this::mapear);
            }
        } catch (SQLException e) {
            throw error("listar las sesiones del entrenador " + idEntrenador, e);
        }
    }

    @Override
    public Sesion guardar(Sesion sesion) {
        if (sesion == null || sesion.getIdSesion() == null || sesion.getIdSesion().isBlank()) {
            throw new IllegalArgumentException("La sesion y su ID son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPSERT)) {
            ps.setString(1, sesion.getIdSesion());
            ps.setString(2, sesion.getIdCliente());
            ps.setString(3, sesion.getIdEntrenador());
            ps.setObject(4, sesion.getHoraInicio());
            ps.setObject(5, sesion.getHoraFin());
            ps.setString(6, sesion.getEstado());
            ps.executeUpdate();
            return sesion;
        } catch (SQLException e) {
            // 23P01 = exclusion_violation: otra peticion agendo al mismo entrenador
            // en ese horario justo antes (carrera que SesionService no alcanza a ver).
            if ("23P01".equals(e.getSQLState())) {
                throw new IllegalStateException("El entrenador ya tiene una sesion en ese horario.");
            }
            if ("22007".equals(e.getSQLState())) {
                throw new IllegalArgumentException(
                        "La hora de inicio debe ser posterior a la fecha y hora actuales.");
            }
            throw error("guardar la sesion " + sesion.getIdSesion(), e);
        }
    }

    private Sesion mapear(ResultSet rs) throws SQLException {
        return new Sesion(
                rs.getString("id_sesion"),
                rs.getString("id_cliente"),
                rs.getString("id_entrenador"),
                rs.getObject("hora_inicio", LocalDateTime.class),
                rs.getObject("hora_fin", LocalDateTime.class),
                rs.getString("estado"));
    }
}
