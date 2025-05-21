package com.example.bankcards.exception;

public class CardAlreadyExistException extends CardException {
    public CardAlreadyExistException(String message) {
        super(message);
    }
}
