package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.PlanDAO;
import com.sv.gimnasio.model.Plan;
import com.sv.gimnasio.model.PlanAnual;
import com.sv.gimnasio.model.PlanMensual;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO de planes sobre PostgreSQL con JDBC (tabla plan).
 * Plan es abstracta: la columna "tipo" (MENSUAL/ANUAL) decide si al leer se
 * crea un PlanMensual o un PlanAnual.
 */
@Repository
public class PlanDAOImpl extends BaseJdbcDAO implements PlanDAO {

    private static final String SELECT_BASE =
            "SELECT id_plan, nombre, tipo, precio_base, descuento FROM plan";

    private static final String SQL_UPSERT =
            "INSERT INTO plan (id_plan, nombre, tipo, precio_base, descuento) "
                    + "VALUES (?, ?, ?, ?, ?) "
                    + "ON CONFLICT (id_plan) DO UPDATE SET "
                    + "nombre = EXCLUDED.nombre, tipo = EXCLUDED.tipo, "
                    + "precio_base = EXCLUDED.precio_base, descuento = EXCLUDED.descuento";

    public PlanDAOImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Plan> obtenerTodos() {
        String sql = SELECT_BASE + " ORDER BY id_plan";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Plan> planes = new ArrayList<>();
            while (rs.next()) {
                planes.add(mapear(rs));
            }
            return planes;
        } catch (SQLException e) {
            throw error("listar planes", e);
        }
    }

    @Override
    public Optional<Plan> obtenerPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        String sql = SELECT_BASE + " WHERE id_plan = ?";
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw error("consultar el plan " + id, e);
        }
    }

    @Override
    public Plan guardar(Plan plan) {
        if (plan == null || plan.getIdPlan() == null || plan.getIdPlan().isBlank()) {
            throw new IllegalArgumentException("El plan y su ID son obligatorios.");
        }
        String tipo = plan instanceof PlanAnual ? "ANUAL" : "MENSUAL";
        double descuento = plan instanceof PlanAnual anual ? anual.getDescuento() : 0.0;
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPSERT)) {
            ps.setString(1, plan.getIdPlan());
            ps.setString(2, plan.getNombre());
            ps.setString(3, tipo);
            ps.setBigDecimal(4, BigDecimal.valueOf(plan.getPrecioBase()).setScale(2, RoundingMode.HALF_UP));
            ps.setBigDecimal(5, BigDecimal.valueOf(descuento).setScale(3, RoundingMode.HALF_UP));
            ps.executeUpdate();
            return plan;
        } catch (SQLException e) {
            throw error("guardar el plan " + plan.getIdPlan(), e);
        }
    }

    private Plan mapear(ResultSet rs) throws SQLException {
        String id = rs.getString("id_plan");
        double precio = rs.getDouble("precio_base");
        Plan plan;
        if ("ANUAL".equals(rs.getString("tipo"))) {
            PlanAnual anual = new PlanAnual(id, precio);
            anual.setDescuento(rs.getDouble("descuento"));
            plan = anual;
        } else {
            plan = new PlanMensual(id, precio);
        }
        plan.setNombre(rs.getString("nombre"));
        return plan;
    }
}
