package com.foober.foober.exception;

public class EmailNotSentException extends RuntimeException {
    public EmailNotSentException() {
    }

    public EmailNotSentException(String message) {
        super(message);
    }

    public EmailNotSentException(String message, Throwable cause) {
        super(message, cause);
    }
}
