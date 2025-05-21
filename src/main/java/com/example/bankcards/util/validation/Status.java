package com.example.bankcards.util.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StatusValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Status {
    String message() default "status must be 'ACTIVE' or 'BLOCKED'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
