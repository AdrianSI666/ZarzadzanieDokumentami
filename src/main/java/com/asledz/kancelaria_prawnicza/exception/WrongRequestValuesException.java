package com.asledz.kancelaria_prawnicza.exception;

public class WrongRequestValuesException extends RuntimeException {
    public WrongRequestValuesException(String message) {
        super(message);
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
