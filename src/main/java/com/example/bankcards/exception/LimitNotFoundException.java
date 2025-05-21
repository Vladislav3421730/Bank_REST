package com.example.bankcards.exception;

public class LimitNotFoundException extends EntityNotFoundException {
    public LimitNotFoundException(String message) {
        super(message);
    }
}
