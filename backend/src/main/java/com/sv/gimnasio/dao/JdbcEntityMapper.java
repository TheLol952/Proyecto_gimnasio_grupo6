package com.sv.gimnasio.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Contrato generico para convertir una fila JDBC en una entidad de dominio. */
@FunctionalInterface
public interface JdbcEntityMapper<T> {

    T mapear(ResultSet resultSet) throws SQLException;
}
