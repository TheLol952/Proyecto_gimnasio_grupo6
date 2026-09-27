package com.sv.gimnasio.dao;

import com.sv.gimnasio.model.Pago;
import java.util.List;

public interface PagoDAO {
    List<Pago> obtenerTodos();
    Pago guardar(Pago pago);
}
