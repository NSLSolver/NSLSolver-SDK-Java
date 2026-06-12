package com.nslsolver.exceptions;

/**
 * Base exception for all NSLSolver API errors.
 * Check {@link #getStatusCode()} for the HTTP status, or 0 for network-level failures.
 */
public class NSLSolverException extends Exception {

    private final int statusCode;
    private final boolean retryable;

    public NSLSolverException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.retryable = false;
    }

    public NSLSolverException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.retryable = false;
    }

    /** Wraps a lower-level error (network timeout, etc.) with no HTTP status. */
    public NSLSolverException(String message, Throwable cause) {
        this(message, cause, false);
    }

    /**
     * Wraps a lower-level error with no HTTP status, marking whether it is a
     * transient failure (e.g. connect/read timeout) the SDK may retry.
     */
    public NSLSolverException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.statusCode = 0;
        this.retryable = retryable;
    }

    /** HTTP status code, or 0 if the error didn't come from an HTTP response. */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * True when the SDK retries this automatically: HTTP 429/503, or a transient
     * network failure (connect/read timeout) flagged as retryable.
     */
    public boolean isRetryable() {
        return statusCode == 429 || statusCode == 503 || retryable;
    }

    @Override
    public String toString() {
        if (statusCode > 0) {
            return String.format("NSLSolverException[%d]: %s", statusCode, getMessage());
        }
        return String.format("NSLSolverException: %s", getMessage());
    }
}
