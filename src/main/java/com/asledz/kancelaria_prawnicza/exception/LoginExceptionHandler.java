package com.asledz.kancelaria_prawnicza.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;

/**
 * Exception handler that handle exceptions when user trys to log in.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class LoginExceptionHandler {
    private static final String MESSAGE = "Failed to log in. Try again later.";
    private final Clock clock;

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {LoginException.class})
    public ResponseEntity<Object> handleLoginException(LoginException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        log.error("Failed to authenticate user with error message: %s".formatted(e.getMessage()), e);
        ExceptionData exceptionData = new ExceptionData(MESSAGE, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
