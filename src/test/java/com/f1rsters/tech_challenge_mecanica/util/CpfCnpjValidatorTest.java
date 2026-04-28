package com.f1rsters.tech_challenge_mecanica.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfCnpjValidatorTest {

    @Test
    void shouldValidateValidCPF() {
        assertTrue(CpfCnpjValidator.isValid("529.982.247-25"));
        assertTrue(CpfCnpjValidator.isValid("52998224725"));
    }

    @Test
    void shouldValidateValidCNPJ() {
        assertTrue(CpfCnpjValidator.isValid("11.444.777/0001-61"));
        assertTrue(CpfCnpjValidator.isValid("11444777000161"));
    }

    @Test
    void shouldInvalidateInvalidCPF() {
        assertFalse(CpfCnpjValidator.isValid("111.111.111-11"));
        assertFalse(CpfCnpjValidator.isValid("123.456.789-00"));
        assertFalse(CpfCnpjValidator.isValid("000.000.000-00"));
    }

    @Test
    void shouldInvalidateInvalidCNPJ() {
        assertFalse(CpfCnpjValidator.isValid("11.111.111/1111-11"));
        assertFalse(CpfCnpjValidator.isValid("00.000.000/0000-00"));
    }

    @Test
    void shouldInvalidateNull() {
        assertFalse(CpfCnpjValidator.isValid(null));
    }

    @Test
    void shouldInvalidateInvalidLength() {
        assertFalse(CpfCnpjValidator.isValid("123456"));
        assertFalse(CpfCnpjValidator.isValid("123456789012345"));
    }

    @Test
    void shouldInvalidateEmptyString() {
        assertFalse(CpfCnpjValidator.isValid(""));
    }
}
