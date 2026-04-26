package com.f1rsters.tech_challenge_mecanica.util;

public final class SensitiveDataMasker {

    private SensitiveDataMasker() {
    }

    public static String maskCpfCnpj(String rawValue) {
        String value = InputNormalizer.normalizeCpfCnpj(rawValue);
        if (value == null) {
            return null;
        }

        if (value.length() == 11) {
            return "***." + value.substring(3, 5) + ".***-" + value.substring(9, 11);
        }

        if (value.length() == 14) {
            return "**.***." + value.substring(5, 8) + "/****-" + value.substring(12, 14);
        }

        return "***";
    }

    public static String maskPlaca(String rawValue) {
        String value = InputNormalizer.normalizePlaca(rawValue);
        if (value == null || value.length() < 3) {
            return "***";
        }

        return value.substring(0, 3) + "****";
    }

    public static String maskEmail(String rawValue) {
        String email = InputNormalizer.normalizeEmail(rawValue);
        if (email == null || email.isBlank() || !email.contains("@")) {
            return "***";
        }

        String[] parts = email.split("@", 2);
        if (parts[0].isBlank()) {
            return "***@" + parts[1];
        }
        return parts[0].charAt(0) + "***@" + parts[1];
    }
}

