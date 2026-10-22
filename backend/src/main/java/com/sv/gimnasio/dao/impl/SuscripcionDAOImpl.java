package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.SuscripcionDAO;
import com.sv.gimnasio.model.Suscripcion;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** DAO de suscripciones sobre PostgreSQL con JDBC (tabla suscripcion). */
@Repository
public class SuscripcionDAOImpl extends BaseJdbcDAO implements SuscripcionDAO {

    private static final String SELECT_BASE =
            "SELECT id_suscripcion, id_cliente, id_plan, fecha_inicio, fecha_fin, estado FROM suscripcion";

    private static final String SQL_UPSERT =
            "INSERT INTO suscripcion (id_suscripcion, id_cliente, id_plan, fecha_inicio, fecha_fin, estado) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (id_suscripcion) DO UPDATE SET "
                    + "id_cliente = EXCLUDED.id_cliente, id_plan = EXCLUDED.id_plan, "
                    + "fecha_inicio = EXCLUDED.fecha_inicio, fecha_fin = EXCLUDED.fecha_fin, "
                    + "estado = EXCLUDED.estado";

    public SuscripcionDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Suscripcion> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY fecha_inicio, id_suscripcion";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return leerLista(rs);
        } catch (SQLException e) {
            throw error("listar suscripciones", e);
        }
    }

    @Override
    public Optional<Suscripcion> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String sql = SELECT_BASE + " WHERE id_suscripcion = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw error("consultar la suscripcion " + id, e);
        }
    }

    @Override
    public List<Suscripcion> obtenerPorCliente(String idCliente) {
        String sql = SELECT_BASE + " WHERE id_cliente = ? ORDER BY fecha_inicio, id_suscripcion";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return leerLista(rs);
            }
        } catch (SQLException e) {
            throw error("listar las suscripciones del cliente " + idCliente, e);
        }
    }

    @Override
    public Suscripcion guardar(Suscripcion suscripcion) {
        if (suscripcion == null || suscripcion.getIdSuscripcion() == null
                || suscripcion.getIdSuscripcion().isBlank()) {
            throw new IllegalArgumentException("La suscripcion y su ID son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPSERT)) {
            ps.setString(1, suscripcion.getIdSuscripcion());
            ps.setString(2, suscripcion.getIdCliente());
            ps.setString(3, suscripcion.getIdPlan());
            ps.setObject(4, suscripcion.getFechaInicio());
            ps.setObject(5, suscripcion.getFechaFin());
            ps.setString(6, suscripcion.getEstado());
            ps.executeUpdate();
            return suscripcion;
        } catch (SQLException e) {
            throw error("guardar la suscripcion " + suscripcion.getIdSuscripcion(), e);
        }
    }

    private List<Suscripcion> leerLista(ResultSet rs) throws SQLException {
        List<Suscripcion> lista = new ArrayList<>();
        while (rs.next()) {
            lista.add(mapear(rs));
        }
        return lista;
    }

    private Suscripcion mapear(ResultSet rs) throws SQLException {
        return new Suscripcion(
                rs.getString("id_suscripcion"),
                rs.getString("id_cliente"),
                rs.getString("id_plan"),
                rs.getObject("fecha_inicio", LocalDate.class),
                rs.getObject("fecha_fin", LocalDate.class),
                rs.getString("estado"));
    }
}
