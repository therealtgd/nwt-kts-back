package com.foober.foober.exception;

public class UserAlreadyActivatedException extends RuntimeException {
    public UserAlreadyActivatedException() {
    }

    public UserAlreadyActivatedException(String message) {
        super(message);
    }

    public UserAlreadyActivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
