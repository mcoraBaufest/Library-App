package com.libraryapp.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    // Verifica que un request inválido se convierta en una respuesta HTTP 400.
    @Test
    void shouldReturnBadRequestWithValidationErrors() {
        Map<String, String> errors = new HashMap<>();
        errors.put("year", "El año no puede ser posterior al año actual");
        InvalidRequestException exception = new InvalidRequestException(errors);
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("La solicitud contiene datos inválidos", response.getBody().get("message"));
        assertEquals(errors, response.getBody().get("errors"));
    }
}
