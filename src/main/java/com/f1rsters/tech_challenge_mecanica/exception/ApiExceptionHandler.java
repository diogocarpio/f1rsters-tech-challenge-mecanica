package com.f1rsters.tech_challenge_mecanica.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        MDC.put("error_type", "MethodArgumentNotValidException");
        MDC.put("error_handler", "ApiExceptionHandler");
        
        try {
            List<Map<String, String>> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(this::toFieldError)
                    .toList();

            log.error("Erro de validação: {} campos inválidos", errors.size());

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "VALIDATION_ERROR");
            body.put("fields", errors);
            return ResponseEntity.badRequest().body(body);
        } finally {
            MDC.clear();
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        MDC.put("error_type", "ConstraintViolationException");
        MDC.put("error_handler", "ApiExceptionHandler");
        
        try {
            List<Map<String, String>> errors = ex.getConstraintViolations()
                    .stream()
                    .map(violation -> {
                        Map<String, String> field = new LinkedHashMap<>();
                        field.put("field", violation.getPropertyPath().toString());
                        field.put("message", violation.getMessage());
                        return field;
                    })
                    .toList();

            log.error("Violação de constraint: {} violações", errors.size());

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "VALIDATION_ERROR");
            body.put("fields", errors);
            return ResponseEntity.badRequest().body(body);
        } finally {
            MDC.clear();
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        MDC.put("error_type", ex.getClass().getSimpleName());
        MDC.put("error_message", ex.getMessage());
        MDC.put("error_handler", "ApiExceptionHandler");
        
        try {
            log.error("Erro de negócio capturado: {}", ex.getMessage());

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "BUSINESS_ERROR");
            body.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(body);
        } finally {
            MDC.clear();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        MDC.put("error_type", ex.getClass().getSimpleName());
        MDC.put("error_message", ex.getMessage());
        MDC.put("error_handler", "ApiExceptionHandler");
        
        try {
            log.error("Erro não tratado capturado pelo handler global", ex);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", "INTERNAL_ERROR");
            body.put("message", "Erro interno no processamento da requisição");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        } finally {
            MDC.clear();
        }
    }

    private Map<String, String> toFieldError(FieldError fieldError) {
        Map<String, String> field = new LinkedHashMap<>();
        field.put("field", fieldError.getField());
        field.put("message", fieldError.getDefaultMessage());
        return field;
    }
}

