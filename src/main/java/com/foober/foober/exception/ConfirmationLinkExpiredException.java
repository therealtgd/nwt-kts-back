package com.foober.foober.exception;

public class ConfirmationLinkExpiredException extends RuntimeException {
    public ConfirmationLinkExpiredException() {
    }

    public ConfirmationLinkExpiredException(String message) {
        super(message);
    }

    public ConfirmationLinkExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
