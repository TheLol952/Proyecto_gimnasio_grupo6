package com.sv.gimnasio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

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

    /** Hace que @Future y las reglas de servicio utilicen la misma zona horaria. */
    @Bean
    public LocalValidatorFactoryBean validator(Clock relojNegocio) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setConfigurationInitializer(
                configuracion -> configuracion.clockProvider(() -> relojNegocio));
        return validator;
    }
}
