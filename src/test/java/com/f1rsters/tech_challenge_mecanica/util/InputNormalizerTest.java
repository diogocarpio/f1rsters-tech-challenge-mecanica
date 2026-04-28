package com.f1rsters.tech_challenge_mecanica.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InputNormalizerTest {

    @Test
    void shouldNormalizeCpfCnpj() {
        assertEquals("12345678901", InputNormalizer.normalizeCpfCnpj("123.456.789-01"));
        assertEquals("12345678901234", InputNormalizer.normalizeCpfCnpj("12.345.678/9012-34"));
        assertEquals("12345678901", InputNormalizer.normalizeCpfCnpj("12345678901"));
    }

    @Test
    void shouldReturnNullForNullCpfCnpj() {
        assertNull(InputNormalizer.normalizeCpfCnpj(null));
    }

    @Test
    void shouldNormalizePlaca() {
        assertEquals("ABC1234", InputNormalizer.normalizePlaca("ABC-1234"));
        assertEquals("ABC1D23", InputNormalizer.normalizePlaca("ABC-1D23"));
        assertEquals("ABC1234", InputNormalizer.normalizePlaca("abc-1234"));
        assertEquals("ABC1234", InputNormalizer.normalizePlaca("ABC1234"));
    }

    @Test
    void shouldReturnNullForNullPlaca() {
        assertNull(InputNormalizer.normalizePlaca(null));
    }

    @Test
    void shouldNormalizeEmail() {
        assertEquals("test@example.com", InputNormalizer.normalizeEmail("  TEST@EXAMPLE.COM  "));
        assertEquals("test@example.com", InputNormalizer.normalizeEmail("Test@Example.Com"));
        assertEquals("test@example.com", InputNormalizer.normalizeEmail("test@example.com"));
    }

    @Test
    void shouldReturnNullForNullEmail() {
        assertNull(InputNormalizer.normalizeEmail(null));
    }
}
