package com.example.bankcards.exception;

public class CardNotFoundException extends EntityNotFoundException {
    public CardNotFoundException(String message) {
        super(message);
    }
}
