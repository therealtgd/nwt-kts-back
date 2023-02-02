package com.foober.foober.exception;

import com.foober.foober.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerAdvisor {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String objectName = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(objectName, errorMessage);
        });
        String mapAsString = errors.keySet().stream()
                .map(key -> key + " " + errors.get(key))
                .collect(Collectors.joining(", ", "", ""));
        return new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Field validation failed.", mapAsString);
    }
    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(TokenExpiredException.class)
    public ApiResponse<?> handleTokenExpiredException(TokenExpiredException e) {
        return new ApiResponse<>(HttpStatus.GONE, "Link has expired.");
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmailNotSentException.class)
    public ApiResponse<?> handleEmailNotSentException(EmailNotSentException e) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ApiResponse<?> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, "You didnt upload a profile image.");
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<?> handleUserNotFoundException(UserNotFoundException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponse<?> handleInvalidTokenException(InvalidTokenException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OAuth2AuthenticationProcessingException.class)
    public ApiResponse<?> handleOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiResponse<?> handleBadRequestException(BadRequestException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, "An error had occurred.");
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<?> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, "Unable to upload. File is too large.");
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SignatureException.class)
    public ApiResponse<?> handleSignatureException(SignatureException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<?> handleNoSuchElementException(NoSuchElementException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ApiResponse<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return new ApiResponse<>(HttpStatus.CONFLICT, e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ApiResponse<?> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return new ApiResponse<>(HttpStatus.CONFLICT, e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ApiResponse<?> handleUsernameAlreadyExistsException(UserAlreadyExistsException e) {
        return new ApiResponse<>(HttpStatus.CONFLICT, e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyActivatedException.class)
    public ApiResponse<?> handleUserAlreadyActivatedException(UserAlreadyActivatedException e) {
        return new ApiResponse<>(HttpStatus.CONFLICT, e.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserIsNotActivatedException.class)
    public ApiResponse<?> handleUserIsNotActivatedException(UserIsNotActivatedException e) {
        return new ApiResponse<>(HttpStatus.CONFLICT, e.getMessage());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnableToGetDriverEtaException.class)
    public ApiResponse<?> handleUnableToGetDriverEtaException(UnableToGetDriverEtaException e) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get driver eta.");
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ClientUnavailable.class)
    public ApiResponse<?> handleClientsInActiveRideException(ClientUnavailable e) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DriverUnavailable.class)
    public ApiResponse<?> handleClientsInActiveRideException(DriverUnavailable e) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ClientHasNoActiveRidesException.class)
    public ApiResponse<?> handleClientHasNoActiveRideException(ClientHasNoActiveRidesException e) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
