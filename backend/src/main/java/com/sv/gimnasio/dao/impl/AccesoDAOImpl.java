package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.AccesoDAO;
import com.sv.gimnasio.model.Acceso;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** DAO de accesos QR sobre PostgreSQL con JDBC (tabla acceso). Es un historial: solo se inserta. */
@Repository
public class AccesoDAOImpl extends BaseJdbcDAO implements AccesoDAO {

    private static final String SQL_INSERT =
            "INSERT INTO acceso (id_acceso, id_cliente, fecha_hora, codigo_qr, estado) VALUES (?, ?, ?, ?, ?)";

    public AccesoDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Acceso> obtenerTodos() {
        String sql = "SELECT id_acceso, id_cliente, fecha_hora, codigo_qr, estado FROM acceso "
                + "ORDER BY fecha_hora, id_acceso";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return leerLista(rs, this::mapear);
        } catch (SQLException e) {
            throw error("listar accesos", e);
        }
    }

    @Override
    public Acceso guardar(Acceso acceso) {
        if (acceso == null || acceso.getIdAcceso() == null || acceso.getIdAcceso().isBlank()) {
            throw new IllegalArgumentException("El acceso y su ID son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, acceso.getIdAcceso());
            ps.setString(2, acceso.getIdCliente());
            ps.setObject(3, acceso.getFechaHora());
            ps.setString(4, acceso.getCodigoQR());
            ps.setString(5, acceso.getEstado());
            ps.executeUpdate();
            return acceso;
        } catch (SQLException e) {
            throw error("guardar el acceso " + acceso.getIdAcceso(), e);
        }
    }

    private Acceso mapear(ResultSet rs) throws SQLException {
        return new Acceso(
                rs.getString("id_acceso"),
                rs.getString("id_cliente"),
                rs.getObject("fecha_hora", LocalDateTime.class),
                rs.getString("codigo_qr"),
                rs.getString("estado"));
    }
}
