package com.eventify.exception;

public class NotFoundException extends ResourceNotFoundException {
    public NotFoundException(String message) {
        super(message);
    }
}
