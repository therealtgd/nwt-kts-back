package com.foober.foober.exception;

public class ClientHasNoActiveRidesException extends RuntimeException {
    public ClientHasNoActiveRidesException(String username) {
        super(username + " has no active rides.");
    }
}
