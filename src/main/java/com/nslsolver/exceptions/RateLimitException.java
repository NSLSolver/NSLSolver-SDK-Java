package com.nslsolver.exceptions;

/** Rate limited (HTTP 429). Retried automatically; thrown only after all retries fail. */
public class RateLimitException extends NSLSolverException {

    public RateLimitException(String message) {
        super(429, message);
    }

    public RateLimitException(String message, Throwable cause) {
        super(429, message, cause);
    }

    @Override
    public boolean isRetryable() {
        return true;
    }
}
