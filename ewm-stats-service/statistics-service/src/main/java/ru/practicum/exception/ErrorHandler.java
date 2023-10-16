package ru.practicum.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(final ValidationException exception) {
        log.error("404 {}", exception.getMessage(), exception);
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final DataNotFoundException e) {
        log.error("404 {}", e.getMessage(), e);
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final DataConflictException e) {
        log.error("409 {}", e.getMessage(), e);
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityException(final DataIntegrityViolationException e) {
        log.error("409 {}", e.getMessage(), e);
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("400 {}", e.getMessage(), e);
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowableExceptions(final Exception e) {
        log.error("500 {}", e.getMessage(), e);
        return new ErrorResponse("error", e.getMessage());
    }

    @Getter
    @RequiredArgsConstructor
    private static class ErrorResponse {
        private final String error;
        private final String description;
    }
}
