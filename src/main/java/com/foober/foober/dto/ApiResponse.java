package com.foober.foober.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private HttpStatusDto status;
    private String message;
    private T body;

    public ApiResponse() {
        this.success = true;
        this.status = new HttpStatusDto(HttpStatus.OK.value(), HttpStatus.OK.name());
        this.message = "OK";
        this.body = null;
    }
    public ApiResponse(String message) {
        this.success = true;
        this.status = new HttpStatusDto(HttpStatus.OK.value(), HttpStatus.OK.name());
        this.message = message;
        this.body = null;
    }

    public ApiResponse(HttpStatus status, String message) {
        this.success = status.value() >= 200 && status.value() < 300;
        this.status = new HttpStatusDto(status.value(), status.name());;
        this.message = message;
        this.body = null;
    }

    public ApiResponse(HttpStatus status, String message, T body) {
        this.success = status.value() >= 200 && status.value() < 300;
        this.status = new HttpStatusDto(status.value(), status.name());;;
        this.message = message;
        this.body = body;
    }

    public ApiResponse(T body) {
        this.body = body;
        this.success = true;
        this.status = new HttpStatusDto(HttpStatus.OK.value(), HttpStatus.OK.name());
        this.message = "";
    }

    public ApiResponse(HttpStatus status) {
        this.success = status.value() >= 200 && status.value() < 300;
        this.status = new HttpStatusDto(status.value(), status.name());;
        this.message = null;
        this.body = null;
    }

}
