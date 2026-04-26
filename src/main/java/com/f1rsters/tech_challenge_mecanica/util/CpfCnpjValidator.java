package com.f1rsters.tech_challenge_mecanica.util;

public final class CpfCnpjValidator {

    private CpfCnpjValidator() {
    }

    public static boolean isValid(String rawValue) {
        String value = InputNormalizer.normalizeCpfCnpj(rawValue);
        if (value == null) {
            return false;
        }

        return switch (value.length()) {
            case 11 -> isValidCpf(value);
            case 14 -> isValidCnpj(value);
            default -> false;
        };
    }

    private static boolean isValidCpf(String cpf) {
        if (allDigitsEqual(cpf)) {
            return false;
        }

        int d1 = calculateCpfDigit(cpf, 9, 10);
        int d2 = calculateCpfDigit(cpf, 10, 11);
        return d1 == (cpf.charAt(9) - '0') && d2 == (cpf.charAt(10) - '0');
    }

    private static int calculateCpfDigit(String cpf, int length, int weightStart) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cpf.charAt(i) - '0') * (weightStart - i);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean isValidCnpj(String cnpj) {
        if (allDigitsEqual(cnpj)) {
            return false;
        }

        int d1 = calculateCnpjDigit(cnpj, 12);
        int d2 = calculateCnpjDigit(cnpj, 13);
        return d1 == (cnpj.charAt(12) - '0') && d2 == (cnpj.charAt(13) - '0');
    }

    private static int calculateCnpjDigit(String cnpj, int length) {
        int sum = 0;
        int weight = 2;

        for (int i = length - 1; i >= 0; i--) {
            sum += (cnpj.charAt(i) - '0') * weight;
            weight = (weight == 9) ? 2 : weight + 1;
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean allDigitsEqual(String value) {
        char first = value.charAt(0);
        for (int i = 1; i < value.length(); i++) {
            if (value.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }
}

