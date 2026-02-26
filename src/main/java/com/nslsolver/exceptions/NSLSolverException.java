package com.nslsolver.exceptions;

/**
 * Base exception for all NSLSolver API errors.
 * Check {@link #getStatusCode()} for the HTTP status, or 0 for network-level failures.
 */
public class NSLSolverException extends Exception {

    private final int statusCode;

    public NSLSolverException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public NSLSolverException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /** Wraps a lower-level error (network timeout, etc.) with no HTTP status. */
    public NSLSolverException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    /** HTTP status code, or 0 if the error didn't come from an HTTP response. */
    public int getStatusCode() {
        return statusCode;
    }

    /** True for 429 and 503 -- the SDK retries these automatically. */
    public boolean isRetryable() {
        return statusCode == 429 || statusCode == 503;
    }

    @Override
    public String toString() {
        if (statusCode > 0) {
            return String.format("NSLSolverException[%d]: %s", statusCode, getMessage());
        }
        return String.format("NSLSolverException: %s", getMessage());
    }
}
