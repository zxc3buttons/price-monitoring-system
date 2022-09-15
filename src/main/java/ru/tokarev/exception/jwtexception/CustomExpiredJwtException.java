package ru.tokarev.exception.jwtexception;

import org.springframework.security.core.AuthenticationException;

public class CustomExpiredJwtException extends AuthenticationException {
    public CustomExpiredJwtException(String msg) {
        super(msg);
    }
}
