package com.example.jewels.errors;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
