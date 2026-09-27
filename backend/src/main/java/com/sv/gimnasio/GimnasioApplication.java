package com.sv.gimnasio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 * Backend: Java 21 + Spring Boot | Comunicacion: REST API | Frontend: Vue.js (proyecto aparte).
 */
@SpringBootApplication
public class GimnasioApplication {
    public static void main(String[] args) {
        SpringApplication.run(GimnasioApplication.class, args);
    }
}
