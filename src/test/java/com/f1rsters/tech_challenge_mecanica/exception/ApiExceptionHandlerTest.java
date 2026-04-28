package com.f1rsters.tech_challenge_mecanica.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        
        List<FieldError> fieldErrors = new ArrayList<>();
        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
        fieldErrors.add(fieldError);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
        assertEquals("VALIDATION_ERROR", response.getBody().get("error"));
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("fieldName");
        when(violation.getMessage()).thenReturn("errorMessage");
        
        when(ex.getConstraintViolations()).thenReturn(java.util.Set.of(violation));
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleConstraintViolation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
        assertEquals("VALIDATION_ERROR", response.getBody().get("error"));
    }
}
