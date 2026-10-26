package com.sv.gimnasio.dao.impl;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BaseJdbcDAOTest {

    @Test
    void mapeaUnaListaGenericaDesdeResultSet() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("valor")).thenReturn("uno", "dos");
        DAOPrueba dao = new DAOPrueba(mock(DataSource.class));

        List<String> valores = dao.leer(resultSet);

        assertEquals(List.of("uno", "dos"), valores);
        verify(resultSet, times(3)).next();
    }

    private static class DAOPrueba extends BaseJdbcDAO {
        DAOPrueba(DataSource dataSource) {
            super(dataSource);
        }

        List<String> leer(ResultSet resultSet) throws Exception {
            return leerLista(resultSet, rs -> rs.getString("valor"));
        }
    }
}
