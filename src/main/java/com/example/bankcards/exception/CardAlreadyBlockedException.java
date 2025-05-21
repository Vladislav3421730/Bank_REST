package com.example.bankcards.exception;

public class CardAlreadyBlockedException extends CardException {
    public CardAlreadyBlockedException(String message) {
        super(message);
    }
}
