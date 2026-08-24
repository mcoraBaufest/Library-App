package com.libraryapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequest(InvalidRequestException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", exception.getMessage());
        response.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
