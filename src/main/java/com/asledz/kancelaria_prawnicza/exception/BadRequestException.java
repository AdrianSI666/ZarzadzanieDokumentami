package com.asledz.kancelaria_prawnicza.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Method to overwrite stack trace, so when exception occurs, which is delegated to frontend,
     * won't fill server log with stack trace of known exception.
     *
     * @return ignored
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
