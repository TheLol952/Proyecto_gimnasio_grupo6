package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.PagoDAO;
import com.sv.gimnasio.model.Pago;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** DAO de pagos sobre PostgreSQL con JDBC (tabla pago). Un pago, una vez registrado, no se edita. */
@Repository
public class PagoDAOImpl extends BaseJdbcDAO implements PagoDAO {

    private static final String SQL_INSERT =
            "INSERT INTO pago (id_pago, id_suscripcion, fecha, monto, metodo_pago) VALUES (?, ?, ?, ?, ?)";

    public PagoDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Pago> obtenerTodos() {
        String sql = "SELECT id_pago, id_suscripcion, fecha, monto, metodo_pago FROM pago ORDER BY fecha, id_pago";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return leerLista(rs, this::mapear);
        } catch (SQLException e) {
            throw error("listar pagos", e);
        }
    }

    @Override
    public Pago guardar(Pago pago) {
        if (pago == null || pago.getIdPago() == null || pago.getIdPago().isBlank()) {
            throw new IllegalArgumentException("El pago y su ID son obligatorios.");
        }
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, pago.getIdPago());
            ps.setString(2, pago.getIdSuscripcion());
            ps.setObject(3, pago.getFecha());
            ps.setBigDecimal(4, BigDecimal.valueOf(pago.getMonto()).setScale(2, RoundingMode.HALF_UP));
            ps.setString(5, pago.getMetodoPago());
            ps.executeUpdate();
            return pago;
        } catch (SQLException e) {
            throw error("guardar el pago " + pago.getIdPago(), e);
        }
    }

    private Pago mapear(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getString("id_pago"),
                rs.getString("id_suscripcion"),
                rs.getObject("fecha", LocalDateTime.class),
                rs.getBigDecimal("monto").doubleValue(),
                rs.getString("metodo_pago"));
    }
}
