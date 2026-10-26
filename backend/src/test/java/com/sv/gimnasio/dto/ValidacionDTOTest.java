package com.sv.gimnasio.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ValidacionDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void detectaHoraDeInicioPasada() {
        SesionRequestDTO dto = new SesionRequestDTO();
        dto.setIdCliente("CLI-1");
        dto.setIdEntrenador("ENT-1");
        dto.setHoraInicio(LocalDateTime.now().minusHours(2));
        dto.setHoraFin(LocalDateTime.now().minusHours(1));

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void rechazaMetodoDePagoDesconocido() {
        SuscripcionRequestDTO dto = new SuscripcionRequestDTO();
        dto.setIdCliente("CLI-1");
        dto.setIdPlan("PLAN-1");
        dto.setMetodoPago("CHEQUE");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void exigeCodigoQr() {
        AccesoRequestDTO dto = new AccesoRequestDTO();
        dto.setIdCliente("CLI-1");
        dto.setCodigoQR(" ");

        assertFalse(validator.validate(dto).isEmpty());
    }
}
