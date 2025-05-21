package com.example.bankcards.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CardNumberValidator implements ConstraintValidator<CardNumber, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.isBlank()) return false;
        return s.matches("^\\d{4} \\d{4} \\d{4} \\d{4}$");
    }
}
