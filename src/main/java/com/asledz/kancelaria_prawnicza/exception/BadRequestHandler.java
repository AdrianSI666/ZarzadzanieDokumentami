package com.asledz.kancelaria_prawnicza.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class BadRequestHandler {
    private static final String MESSAGE = "Given data cannot be processed due to conflict with existing data";
    private final Clock clock;

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleDataConflict(BadRequestException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        log.error("Exception in processing request occurred: " + e.getMessage(), e);
        ExceptionData exceptionData = new ExceptionData(MESSAGE, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
