package com.f1rsters.tech_challenge_mecanica.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SensitiveDataMaskerTest {

    @Test
    void shouldMaskCPF() {
        assertEquals("***.45.***-01", SensitiveDataMasker.maskCpfCnpj("123.456.789-01"));
        assertEquals("***.45.***-01", SensitiveDataMasker.maskCpfCnpj("12345678901"));
    }

    @Test
    void shouldMaskCNPJ() {
        assertEquals("**.***.789/****-34", SensitiveDataMasker.maskCpfCnpj("12.345.789/9012-34"));
        assertEquals("**.***.789/****-34", SensitiveDataMasker.maskCpfCnpj("12345678901234"));
    }

    @Test
    void shouldReturnNullForNullCpfCnpj() {
        assertNull(SensitiveDataMasker.maskCpfCnpj(null));
    }

    @Test
    void shouldReturnDefaultMaskForInvalidCpfCnpj() {
        assertEquals("***", SensitiveDataMasker.maskCpfCnpj("123456"));
    }

    @Test
    void shouldMaskPlaca() {
        assertEquals("ABC****", SensitiveDataMasker.maskPlaca("ABC1234"));
        assertEquals("ABC****", SensitiveDataMasker.maskPlaca("ABC-1234"));
        assertEquals("ABC****", SensitiveDataMasker.maskPlaca("abc1234"));
    }

    @Test
    void shouldReturnDefaultMaskForShortPlaca() {
        assertEquals("***", SensitiveDataMasker.maskPlaca("AB"));
        assertEquals("***", SensitiveDataMasker.maskPlaca(null));
    }

    @Test
    void shouldMaskEmail() {
        assertEquals("t***@example.com", SensitiveDataMasker.maskEmail("test@example.com"));
        assertEquals("t***@example.com", SensitiveDataMasker.maskEmail("TEST@EXAMPLE.COM"));
        assertEquals("t***@example.com", SensitiveDataMasker.maskEmail("  TEST@EXAMPLE.COM  "));
    }

    @Test
    void shouldReturnDefaultMaskForInvalidEmail() {
        assertEquals("***", SensitiveDataMasker.maskEmail(null));
        assertEquals("***", SensitiveDataMasker.maskEmail(""));
        assertEquals("***", SensitiveDataMasker.maskEmail("invalidemail"));
    }

    @Test
    void shouldMaskEmailWithEmptyLocalPart() {
        assertEquals("***@example.com", SensitiveDataMasker.maskEmail("@example.com"));
    }
}
