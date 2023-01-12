package com.foober.foober.exception;

import com.foober.foober.exception.error.ExceptionResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmailNotSentException.class)
    public ExceptionResponseBody handleEmailNotSentException(EmailNotSentException e) {
        return new ExceptionResponseBody(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage());
    }
    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(EmailNotSentException.class)
    public ExceptionResponseBody handleConfirmationLinkExpiredException(ConfirmationLinkExpiredException e) {
        return new ExceptionResponseBody(
                HttpStatus.GONE.value(),
                e.getMessage());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmailNotSentException.class)
    public ExceptionResponseBody handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ExceptionResponseBody(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailNotSentException.class)
    public ExceptionResponseBody handleUserNotFoundException(UserNotFoundException e) {
        return new ExceptionResponseBody(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ExceptionResponseBody handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return new ExceptionResponseBody(
                HttpStatus.CONFLICT.value(),
                ex.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ExceptionResponseBody handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return new ExceptionResponseBody(
                HttpStatus.CONFLICT.value(),
                ex.getMessage());
    }
}
