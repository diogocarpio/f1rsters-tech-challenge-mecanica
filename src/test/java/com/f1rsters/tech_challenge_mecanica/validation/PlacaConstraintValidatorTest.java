package com.f1rsters.tech_challenge_mecanica.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PlacaConstraintValidatorTest {

    @Test
    void shouldValidateValidPadraoBrPlaca() {
        PlacaConstraintValidator validator = new PlacaConstraintValidator();
        validator.initialize(mock(PlacaValida.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("ABC1234", context));
        assertTrue(validator.isValid("ABC-1234", context));
    }

    @Test
    void shouldValidateValidMercosulPlaca() {
        PlacaConstraintValidator validator = new PlacaConstraintValidator();
        validator.initialize(mock(PlacaValida.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("ABC1D23", context));
        assertTrue(validator.isValid("ABC-1D23", context));
    }

    @Test
    void shouldReturnTrueForNull() {
        PlacaConstraintValidator validator = new PlacaConstraintValidator();
        validator.initialize(mock(PlacaValida.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueForBlankString() {
        PlacaConstraintValidator validator = new PlacaConstraintValidator();
        validator.initialize(mock(PlacaValida.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    void shouldInvalidateInvalidPlaca() {
        PlacaConstraintValidator validator = new PlacaConstraintValidator();
        validator.initialize(mock(PlacaValida.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("ABC123", context) == false);
        assertTrue(validator.isValid("ABC12345", context) == false);
        assertTrue(validator.isValid("1234567", context) == false);
    }

    @Test
    void shouldInvalidateInvalidCharacters() {
        PlacaConstraintValidator validator = new PlacaConstraintValidator();
        validator.initialize(mock(PlacaValida.class));
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        
        assertTrue(validator.isValid("AB@1234", context) == false);
        assertTrue(validator.isValid("ABC-12*4", context) == false);
    }
}
