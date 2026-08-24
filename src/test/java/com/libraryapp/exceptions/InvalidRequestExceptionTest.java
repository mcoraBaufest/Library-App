package com.libraryapp.exceptions;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class InvalidRequestExceptionTest {

    // Verifica que la excepción conserve el mensaje y los errores recibidos.
    @Test
    void shouldStoreValidationErrors() {
        Map<String, String> errors = new HashMap<>();
        errors.put("title", "El título es obligatorio");

        InvalidRequestException exception = new InvalidRequestException(errors);

        assertEquals("La solicitud contiene datos inválidos", exception.getMessage());
        assertSame(errors, exception.getErrors());
    }
}
