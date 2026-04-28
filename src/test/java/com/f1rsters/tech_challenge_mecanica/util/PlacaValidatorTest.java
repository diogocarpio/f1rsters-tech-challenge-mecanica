package com.f1rsters.tech_challenge_mecanica.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacaValidatorTest {

    @Test
    void shouldValidateValidPadraoBrPlaca() {
        assertTrue(PlacaValidator.isValid("ABC1234"));
        assertTrue(PlacaValidator.isValid("ABC-1234"));
        assertTrue(PlacaValidator.isValid("abc1234"));
    }

    @Test
    void shouldValidateValidMercosulPlaca() {
        assertTrue(PlacaValidator.isValid("ABC1D23"));
        assertTrue(PlacaValidator.isValid("ABC-1D23"));
        assertTrue(PlacaValidator.isValid("abc1d23"));
    }

    @Test
    void shouldInvalidateInvalidPlaca() {
        assertFalse(PlacaValidator.isValid("ABC123"));
        assertFalse(PlacaValidator.isValid("ABC12345"));
        assertFalse(PlacaValidator.isValid("1234567"));
        assertFalse(PlacaValidator.isValid("ABCDEFG"));
    }

    @Test
    void shouldInvalidateNull() {
        assertFalse(PlacaValidator.isValid(null));
    }

    @Test
    void shouldInvalidateEmptyString() {
        assertFalse(PlacaValidator.isValid(""));
    }

    @Test
    void shouldInvalidateInvalidCharacters() {
        assertFalse(PlacaValidator.isValid("AB@1234"));
        assertFalse(PlacaValidator.isValid("ABC-12*4"));
    }
}
