package com.example.bankcards.exception;

public class EncryptionFailedException extends RuntimeException {
    public EncryptionFailedException(String message) {
        super(message);
    }
}
