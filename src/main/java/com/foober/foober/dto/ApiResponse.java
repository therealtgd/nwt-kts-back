package com.foober.foober.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiResponse<T> {
    private final boolean success;
    private final HttpStatus status;
    private final String message;
    private final T body;

    public ApiResponse(String message) {
        this.success = true;
        this.status = HttpStatus.OK;
        this.message = message;
        this.body = null;
    }

    public ApiResponse(HttpStatus status, String message) {
        this.success = status.value() >= 200 && status.value() < 300;
        this.status = status;
        this.message = message;
        this.body = null;
    }

    public ApiResponse(T body) {
        this.body = body;
        this.success = true;
        this.status = HttpStatus.OK;
        this.message = "";
    }

    public HttpStatusDto getStatus() {
        return new HttpStatusDto(status.value(), status.name());
    }

    private record HttpStatusDto(int value, String name) {
    }
}
