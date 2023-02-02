package com.foober.foober.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientUnavailable extends RuntimeException {

    public ClientUnavailable(String clients) {
        super("Clients unavailable: " + clients);
    }
}
