package com.example.bankcards.util.validation;

import com.example.bankcards.model.enums.CardStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<Status, String> {
    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
        return str.equals(CardStatus.ACTIVE.name()) || str.equals(CardStatus.BLOCKED.name());
    }
}
