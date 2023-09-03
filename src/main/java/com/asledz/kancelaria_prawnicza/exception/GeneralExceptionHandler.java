package com.asledz.kancelaria_prawnicza.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;

/**
 * Class with methods to wrap exception with additional data and prepare it to be sent outside to user.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GeneralExceptionHandler {
    private final Clock clock;

    /**
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status code 409 meaning exception occurred while
     * processing data.
     */
    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleDataConflict(BadRequestException e) {
        log.error("Exception in processing request occurred: %s, at %s".formatted(e.getMessage(), clock.instant().toString()), e);
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        String message = "Given data cannot be processed due to conflict with existing data";
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status code 403 meaning you don't have
     * authorities for requested resources.
     */
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String message = "You don't have permission for this operation.";
        log.error("Request with too small permissions occurred: %s, at %s".formatted(e.getMessage(), clock.instant().toString()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status code 401 - provided token/login data is
     * incorrect.
     */
    @ExceptionHandler(value = {LoginException.class})
    public ResponseEntity<Object> handleLoginException(LoginException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String message = "Failed to log in. Try again later.";
        log.error("Failed to authenticate user with error message: %s, at %s".formatted(e.getMessage(), clock.instant().toString()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status code 404 - requested resource doesn't
     * exist.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String message = "The requested data could not be found";
        log.error("Object not found. Exception occurred: %s, at %s".formatted(e.getMessage(), clock.instant().toString()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status code 400 - request have wrong data and
     * can't be processed.
     */
    @ExceptionHandler(value = {WrongRequestValuesException.class})
    public ResponseEntity<Object> handleWrongRequestValuesException(NotFoundException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "The requested method can't process given data.";
        log.error("The requested method can't process given data: %s, at %s".formatted(e.getMessage(), clock.instant().toString()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

}
