package com.f1rsters.tech_challenge_mecanica.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDATION_ERROR");
        body.put("fields", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> {
                    Map<String, String> field = new LinkedHashMap<>();
                    field.put("field", violation.getPropertyPath().toString());
                    field.put("message", violation.getMessage());
                    return field;
                })
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDATION_ERROR");
        body.put("fields", errors);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, String> toFieldError(FieldError fieldError) {
        Map<String, String> field = new LinkedHashMap<>();
        field.put("field", fieldError.getField());
        field.put("message", fieldError.getDefaultMessage());
        return field;
    }
}

