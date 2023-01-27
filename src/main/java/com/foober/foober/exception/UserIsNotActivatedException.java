package com.foober.foober.exception;

public class UserIsNotActivatedException extends RuntimeException {
    public UserIsNotActivatedException() {
    }

    public UserIsNotActivatedException(String message) {
        super(message);
    }

    public UserIsNotActivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
