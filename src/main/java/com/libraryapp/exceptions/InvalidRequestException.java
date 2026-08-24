package com.libraryapp.exceptions;

import java.util.Map;

public class InvalidRequestException extends RuntimeException {

    private final Map<String, String> errors;

    public InvalidRequestException(Map<String, String> errors) {
        super("La solicitud contiene datos inválidos");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
