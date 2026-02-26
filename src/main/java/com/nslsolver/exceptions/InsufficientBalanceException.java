package com.nslsolver.exceptions;

/** Account balance too low (HTTP 402). Add funds before retrying. */
public class InsufficientBalanceException extends NSLSolverException {

    public InsufficientBalanceException(String message) {
        super(402, message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(402, message, cause);
    }
}
