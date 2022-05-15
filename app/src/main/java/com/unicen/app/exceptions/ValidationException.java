package com.unicen.app.exceptions;

/**
 * Deprecated. Use {@link CoreApiException} instead
 */
@Deprecated
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
