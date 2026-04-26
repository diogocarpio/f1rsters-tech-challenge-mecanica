package com.f1rsters.tech_challenge_mecanica.validation;

import com.f1rsters.tech_challenge_mecanica.util.CpfCnpjValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjConstraintValidator implements ConstraintValidator<CpfCnpjValido, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return CpfCnpjValidator.isValid(value);
    }
}

