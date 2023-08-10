package com.asledz.kancelaria_prawnicza.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneralExceptionHandlerTest {
    /**
     * Method under test: {@link GeneralExceptionHandler#handleDataConflict(BadRequestException)}
     */
    @Test
    void testHandleDataConflict() {
        GeneralExceptionHandler generalExceptionHandler = new GeneralExceptionHandler(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        ResponseEntity<Object> actualHandleDataConflictResult = generalExceptionHandler
                .handleDataConflict(new BadRequestException("An error occurred"));
        assertTrue(actualHandleDataConflictResult.hasBody());
        assertTrue(actualHandleDataConflictResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.CONFLICT, actualHandleDataConflictResult.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, ((ExceptionData) actualHandleDataConflictResult.getBody()).httpStatus());
        assertEquals("Given data cannot be processed due to conflict with existing data",
                ((ExceptionData) actualHandleDataConflictResult.getBody()).message());
    }

    /**
     * Method under test: {@link GeneralExceptionHandler#handleForbiddenException(ForbiddenException)}
     */
    @Test
    void testHandleForbiddenException() {
        GeneralExceptionHandler generalExceptionHandler = new GeneralExceptionHandler(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        ResponseEntity<Object> actualHandleForbiddenExceptionResult = generalExceptionHandler
                .handleForbiddenException(new ForbiddenException("An error occurred"));
        assertTrue(actualHandleForbiddenExceptionResult.hasBody());
        assertTrue(actualHandleForbiddenExceptionResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.FORBIDDEN, actualHandleForbiddenExceptionResult.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, ((ExceptionData) actualHandleForbiddenExceptionResult.getBody()).httpStatus());
        assertEquals("You don't have permission for this operation.",
                ((ExceptionData) actualHandleForbiddenExceptionResult.getBody()).message());
    }

    /**
     * Method under test: {@link GeneralExceptionHandler#handleLoginException(LoginException)}
     */
    @Test
    void testHandleLoginException() {
        GeneralExceptionHandler generalExceptionHandler = new GeneralExceptionHandler(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        ResponseEntity<Object> actualHandleLoginExceptionResult = generalExceptionHandler
                .handleLoginException(new LoginException("Msg", new Throwable()));
        assertTrue(actualHandleLoginExceptionResult.hasBody());
        assertTrue(actualHandleLoginExceptionResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.UNAUTHORIZED, actualHandleLoginExceptionResult.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, ((ExceptionData) actualHandleLoginExceptionResult.getBody()).httpStatus());
        assertEquals("Failed to log in. Try again later.",
                ((ExceptionData) actualHandleLoginExceptionResult.getBody()).message());
    }

    /**
     * Method under test: {@link GeneralExceptionHandler#handleNotFoundException(NotFoundException)}
     */
    @Test
    void testHandleNotFoundException() {
        GeneralExceptionHandler generalExceptionHandler = new GeneralExceptionHandler(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        ResponseEntity<Object> actualHandleNotFoundExceptionResult = generalExceptionHandler
                .handleNotFoundException(new NotFoundException("An error occurred"));
        assertTrue(actualHandleNotFoundExceptionResult.hasBody());
        assertTrue(actualHandleNotFoundExceptionResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.NOT_FOUND, actualHandleNotFoundExceptionResult.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, ((ExceptionData) actualHandleNotFoundExceptionResult.getBody()).httpStatus());
        assertEquals("The requested data could not be found",
                ((ExceptionData) actualHandleNotFoundExceptionResult.getBody()).message());
    }

    /**
     * Method under test: {@link GeneralExceptionHandler#handleWrongRequestValuesException(NotFoundException)}
     */
    @Test
    void testHandleWrongRequestValuesException() {
        GeneralExceptionHandler generalExceptionHandler = new GeneralExceptionHandler(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        ResponseEntity<Object> actualHandleWrongRequestValuesExceptionResult = generalExceptionHandler
                .handleWrongRequestValuesException(new NotFoundException("An error occurred"));
        assertTrue(actualHandleWrongRequestValuesExceptionResult.hasBody());
        assertTrue(actualHandleWrongRequestValuesExceptionResult.getHeaders().isEmpty());
        assertEquals(HttpStatus.BAD_REQUEST, actualHandleWrongRequestValuesExceptionResult.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,
                ((ExceptionData) actualHandleWrongRequestValuesExceptionResult.getBody()).httpStatus());
        assertEquals("The requested method can't process given data.",
                ((ExceptionData) actualHandleWrongRequestValuesExceptionResult.getBody()).message());
    }
}

