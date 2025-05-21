package com.example.bankcards.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BlockRequestStatusValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BlockRequestStatus {
    String message() default "status must be 'COMPLETED' or 'REJECTED'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
