package com.f1rsters.tech_challenge_mecanica.util;

import java.util.regex.Pattern;

public final class PlacaValidator {

    private static final Pattern PLACA_BR_PADRAO = Pattern.compile("^[A-Z]{3}[0-9]{4}$");
    private static final Pattern PLACA_MERCOSUL = Pattern.compile("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

    private PlacaValidator() {
    }

    public static boolean isValid(String rawValue) {
        String value = InputNormalizer.normalizePlaca(rawValue);
        if (value == null || value.length() != 7) {
            return false;
        }

        return PLACA_BR_PADRAO.matcher(value).matches() || PLACA_MERCOSUL.matcher(value).matches();
    }
}

