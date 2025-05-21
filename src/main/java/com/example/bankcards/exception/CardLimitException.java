package com.example.bankcards.exception;

public class CardLimitException extends CardException {
    public CardLimitException(String message) {
        super(message);
    }
}
