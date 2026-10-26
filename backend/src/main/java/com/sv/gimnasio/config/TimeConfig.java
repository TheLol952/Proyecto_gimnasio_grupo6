package com.sv.gimnasio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** Configuracion central de la zona horaria utilizada por las reglas de negocio. */
@Configuration
public class TimeConfig {

    public static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/El_Salvador");

    @Bean
    public Clock relojNegocio() {
        return Clock.system(ZONA_NEGOCIO);
    }
}
