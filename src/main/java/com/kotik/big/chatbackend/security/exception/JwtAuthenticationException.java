package com.kotik.big.chatbackend.security.exception;

public class JwtAuthenticationException extends Exception {
    public JwtAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JwtAuthenticationException(String message) {
        super(message);
    }
}
