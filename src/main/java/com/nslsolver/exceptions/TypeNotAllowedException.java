package com.nslsolver.exceptions;

/** Captcha type not enabled for this account (HTTP 403). Contact support or upgrade. */
public class TypeNotAllowedException extends NSLSolverException {

    public TypeNotAllowedException(String message) {
        super(403, message);
    }

    public TypeNotAllowedException(String message, Throwable cause) {
        super(403, message, cause);
    }
}
