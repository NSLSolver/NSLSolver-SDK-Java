package com.nslsolver.exceptions;

/** Invalid or missing API key (HTTP 401). Not retryable. */
public class AuthenticationException extends NSLSolverException {

    public AuthenticationException(String message) {
        super(401, message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(401, message, cause);
    }
}
