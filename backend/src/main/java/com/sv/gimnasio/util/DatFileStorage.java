package com.sv.gimnasio.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad generica de PERSISTENCIA sobre archivos .dat mediante serializacion
 * de objetos Java (ObjectOutputStream/ObjectInputStream), tal como se definio
 * en el punto 9 del avance: no se usa base de datos en esta etapa del proyecto.
 *
 * Cada DAO crea una instancia de esta clase apuntando a su propio archivo
 * (por ejemplo data/clientes.dat) y la usa para leer/escribir la lista
 * completa de objetos de ese tipo.
 */
public class DatFileStorage<T extends Serializable> {

    private final String rutaArchivo;

    public DatFileStorage(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<T> leerTodos() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object contenido = ois.readObject();
            return (List<T>) contenido;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("No fue posible leer el archivo " + rutaArchivo, e);
        }
    }

    public synchronized void guardarTodos(List<T> datos) {
        File archivo = new File(rutaArchivo);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(new ArrayList<>(datos));
        } catch (IOException e) {
            throw new RuntimeException("No fue posible escribir el archivo " + rutaArchivo, e);
        }
    }
}
