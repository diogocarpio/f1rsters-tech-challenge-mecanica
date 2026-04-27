package com.f1rsters.tech_challenge_mecanica.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = com.f1rsters.tech_challenge_mecanica.validation.CpfCnpjConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfCnpjValido {
    String message() default "cpfCnpj invalido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

