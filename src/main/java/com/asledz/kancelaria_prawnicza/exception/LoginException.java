package com.asledz.kancelaria_prawnicza.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginException extends AuthenticationException {
    public LoginException(String msg) {
        super(msg);
    }

    public LoginException(String msg, Throwable cause) {
        super(msg, cause);
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
