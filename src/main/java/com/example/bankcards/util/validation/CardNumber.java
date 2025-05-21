package com.example.bankcards.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CardNumberValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CardNumber {
    String message() default "Card number must be in the format '#### #### #### ####";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
