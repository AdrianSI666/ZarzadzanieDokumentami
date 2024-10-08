package com.asledz.kancelaria_prawnicza.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Record for data that will be sent when exception is thrown.
 *
 * @param message    exception message
 * @param httpStatus status code of exception thrown which determines the type of exception thrown
 * @param instant    time in UTC showing when this exception occurred
 */
public record ExceptionData(String message,
                            HttpStatus httpStatus,
                            Instant instant) {
}

