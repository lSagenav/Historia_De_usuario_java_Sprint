package com.eventify.exception;

public class BadRequestException extends BusinessRuleViolationException {
    public BadRequestException(String message) {
        super(message);
    }
}
