package com.f1rsters.tech_challenge_mecanica.validation;

import com.f1rsters.tech_challenge_mecanica.util.PlacaValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlacaConstraintValidator implements ConstraintValidator<PlacaValida, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return PlacaValidator.isValid(value);
    }
}

