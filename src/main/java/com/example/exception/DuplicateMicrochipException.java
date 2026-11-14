package com.example.exception;

public class DuplicateMicrochipException extends RuntimeException {
    public DuplicateMicrochipException(String message) {
        super(message);
    }
}