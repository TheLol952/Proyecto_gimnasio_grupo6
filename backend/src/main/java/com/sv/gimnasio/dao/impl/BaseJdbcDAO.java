package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.exception.PersistenciaException;
import com.sv.gimnasio.dao.JdbcEntityMapper;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base comun de los DAO JDBC. Reune lo que antes habria estado repetido en
 * los ocho DAO: como obtener la Connection y como traducir una SQLException.
 *
 * BAJO ACOPLAMIENTO: los DAO dependen de javax.sql.DataSource (interfaz), no
 * de una clase de pool concreta. Quien configure el pool (DataSourceConfig)
 * puede cambiar de implementacion sin tocar los DAO.
 *
 * TransactionAwareDataSourceProxy: si el servicio esta anotado con
 * @Transactional, getConnection() devuelve la conexion de esa transaccion y
 * close() no la cierra todavia; si no hay transaccion, funciona como un
 * DataSource normal (cada operacion toma y devuelve su conexion al pool).
 */
public abstract class BaseJdbcDAO {

    private final DataSource dataSource;

    protected BaseJdbcDAO(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
    }

    /** Siempre se usa dentro de un try-with-resources en el DAO que la pide. */
    protected Connection abrirConexion() throws SQLException {
        return dataSource.getConnection();
    }

    /** Reutiliza el recorrido de ResultSet sin acoplar la clase base a una entidad. */
    protected <T> List<T> leerLista(ResultSet resultSet, JdbcEntityMapper<T> mapper)
            throws SQLException {
        List<T> resultados = new ArrayList<>();
        while (resultSet.next()) {
            resultados.add(mapper.mapear(resultSet));
        }
        return resultados;
    }

    /** Convierte SQLException en PersistenciaException conservando la causa. */
    protected PersistenciaException error(String operacion, SQLException e) {
        String estado = e.getSQLState();
        boolean temporal = e instanceof SQLTransientException
                || (estado != null && (estado.startsWith("08") || estado.startsWith("53")
                || estado.startsWith("57")));
        return new PersistenciaException(
                "Error de base de datos al " + operacion + " [SQLState=" + estado + "]", e, temporal);
    }
}
