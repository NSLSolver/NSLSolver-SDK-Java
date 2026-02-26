package com.nslsolver.exceptions;

/** Bad request (400) or backend error (503). 503 is retried automatically. */
public class SolveException extends NSLSolverException {

    public SolveException(int statusCode, String message) {
        super(statusCode, message);
    }

    public SolveException(int statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }
}
