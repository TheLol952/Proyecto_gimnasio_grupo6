package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.ClienteDAO;
import com.sv.gimnasio.model.Cliente;
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

/** DAO de clientes sobre PostgreSQL con JDBC (tabla cliente). */
@Repository
public class ClienteDAOImpl extends BaseJdbcDAO implements ClienteDAO {

    private static final String SELECT_BASE =
            "SELECT id_cliente, nombre, apellido, telefono, correo, fecha_registro FROM cliente";

    private static final String SQL_UPSERT =
            "INSERT INTO cliente (id_cliente, nombre, apellido, telefono, correo, fecha_registro) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (id_cliente) DO UPDATE SET "
                    + "nombre = EXCLUDED.nombre, apellido = EXCLUDED.apellido, "
                    + "telefono = EXCLUDED.telefono, correo = EXCLUDED.correo";

    public ClienteDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Cliente> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY fecha_registro, id_cliente";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> clientes = new ArrayList<>();
            while (rs.next()) {
                clientes.add(mapear(rs));
            }
            return clientes;
        } catch (SQLException e) {
            throw error("listar clientes", e);
        }
    }

    @Override
    public Optional<Cliente> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String sql = SELECT_BASE + " WHERE id_cliente = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw error("consultar el cliente " + id, e);
        }
    }

    /** Inserta el cliente o, si el id ya existe, actualiza sus datos editables. */
    @Override
    public Cliente guardar(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() == null || cliente.getIdCliente().isBlank()) {
            throw new IllegalArgumentException("El cliente y su ID son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPSERT)) {
            ps.setString(1, cliente.getIdCliente());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getCorreo());
            ps.setObject(6, cliente.getFechaRegistro() != null ? cliente.getFechaRegistro() : LocalDate.now());
            ps.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            throw error("guardar el cliente " + cliente.getIdCliente(), e);
        }
    }

    @Override
    public void eliminar(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) { // foreign_key_violation
                throw new IllegalStateException(
                        "No se puede eliminar el cliente porque tiene suscripciones o sesiones asociadas.");
            }
            throw error("eliminar el cliente " + id, e);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("id_cliente"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getObject("fecha_registro", LocalDate.class));
    }
}
