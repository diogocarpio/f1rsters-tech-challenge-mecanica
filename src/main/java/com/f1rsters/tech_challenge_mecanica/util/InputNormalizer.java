package com.f1rsters.tech_challenge_mecanica.util;

import java.util.Locale;

public final class InputNormalizer {

    private InputNormalizer() {
    }

    public static String normalizeCpfCnpj(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\\D", "");
    }

    public static String normalizePlaca(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    public static String normalizeEmail(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}

