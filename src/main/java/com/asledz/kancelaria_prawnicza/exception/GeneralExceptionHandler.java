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
public class GeneralExceptionHandler {
    private final Clock clock;

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleDataConflict(BadRequestException e) {
        log.error("Exception in processing request occurred: " + e.getMessage(), e);
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        String message = "Given data cannot be processed due to conflict with existing data";
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String message = "You don't have permission for this operation.";
        log.error("Request with too small permissions occurred: %s".formatted(e.getMessage()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {LoginException.class})
    public ResponseEntity<Object> handleLoginException(LoginException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String message = "Failed to log in. Try again later.";
        log.error("Failed to authenticate user with error message: %s".formatted(e.getMessage()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }

    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String message = "The requested data could not be found";
        log.error("Object not found. Exception occurred: %s".formatted(e.getMessage()), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }


    /**
     * Method to wrap exception with additional data and prepare it to be sent outside to user.
     *
     * @param e exception that caused handler to be invoked.
     * @return ResponseEntity with containing exception details and http status.
     */
    @ExceptionHandler(value = {ServerErrorException.class})
    public ResponseEntity<Object> handleServerError(BadRequestException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        String message = "There was error on the server while processing your request. Try again later.";
        log.error("Runtime exception occurred: " + e.getMessage(), e);
        ExceptionData exceptionData = new ExceptionData(message, httpStatus, clock.instant());
        return new ResponseEntity<>(exceptionData, httpStatus);
    }
}
