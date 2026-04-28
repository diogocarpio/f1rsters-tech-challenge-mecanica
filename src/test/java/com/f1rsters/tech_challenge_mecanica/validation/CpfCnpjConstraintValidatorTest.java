package com.f1rsters.tech_challenge_mecanica.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CpfCnpjConstraintValidatorTest {

    @Test
    void shouldValidateValidCPF() {
        CpfCnpjConstraintValidator validator = new CpfCnpjConstraintValidator();
        validator.initialize(mock(CpfCnpjValido.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("529.982.247-25", context));
        assertTrue(validator.isValid("52998224725", context));
    }

    @Test
    void shouldValidateValidCNPJ() {
        CpfCnpjConstraintValidator validator = new CpfCnpjConstraintValidator();
        validator.initialize(mock(CpfCnpjValido.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("11.444.777/0001-61", context));
        assertTrue(validator.isValid("11444777000161", context));
    }

    @Test
    void shouldReturnTrueForNull() {
        CpfCnpjConstraintValidator validator = new CpfCnpjConstraintValidator();
        validator.initialize(mock(CpfCnpjValido.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueForBlankString() {
        CpfCnpjConstraintValidator validator = new CpfCnpjConstraintValidator();
        validator.initialize(mock(CpfCnpjValido.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    void shouldInvalidateInvalidCPF() {
        CpfCnpjConstraintValidator validator = new CpfCnpjConstraintValidator();
        validator.initialize(mock(CpfCnpjValido.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("111.111.111-11", context) == false);
        assertTrue(validator.isValid("123.456.789-00", context) == false);
    }

    @Test
    void shouldInvalidateInvalidCNPJ() {
        CpfCnpjConstraintValidator validator = new CpfCnpjConstraintValidator();
        validator.initialize(mock(CpfCnpjValido.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("11.111.111/1111-11", context) == false);
    }
}
