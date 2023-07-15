package com.asledz.kancelaria_prawnicza.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginException extends AuthenticationException {
    public LoginException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
