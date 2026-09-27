package com.sv.gimnasio.dao.impl;

import com.sv.gimnasio.dao.PagoDAO;
import com.sv.gimnasio.model.Pago;
import com.sv.gimnasio.util.DatFileStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PagoDAOImpl implements PagoDAO {

    @Value("${app.data.dir:./data}")
    private String dataDir;

    private DatFileStorage<Pago> storage;
    private final List<Pago> pagos = new ArrayList<>();

    @PostConstruct
    public void init() {
        storage = new DatFileStorage<>(dataDir + "/pagos.dat");
        pagos.addAll(storage.leerTodos());
    }

    @Override
    public List<Pago> obtenerTodos() {
        return new ArrayList<>(pagos);
    }

    @Override
    public synchronized Pago guardar(Pago pago) {
        pagos.add(pago);
        storage.guardarTodos(pagos);
        return pago;
    }
}
