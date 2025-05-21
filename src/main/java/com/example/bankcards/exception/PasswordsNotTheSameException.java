package com.example.bankcards.exception;

public class PasswordsNotTheSameException extends RuntimeException {
    public PasswordsNotTheSameException(String message) {
        super(message);
    }
}
