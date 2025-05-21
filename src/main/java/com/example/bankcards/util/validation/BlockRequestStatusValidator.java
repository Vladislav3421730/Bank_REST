package com.example.bankcards.util.validation;

import com.example.bankcards.model.enums.BlockStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BlockRequestStatusValidator implements ConstraintValidator<BlockRequestStatus, String> {
    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {
        return str.equals(BlockStatus.COMPLETED.name()) || str.equals(BlockStatus.REJECTED.name());
    }
}
