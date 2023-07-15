package com.asledz.kancelaria_prawnicza.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;

/**
 * Exception handler that handle Forbidden exceptions that occurs when
 * pearson doesn't have permissions to requested data.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ForbiddenExceptionHandler {
    private static final String MESSAGE = "You don't have permission for this operation.";
    private final Clock clock;

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        log.error("Request with too small permissions occurred: %s".formatted(e.getMessage()), e);
        ExceptionData exceptionData = new ExceptionData(MESSAGE, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
