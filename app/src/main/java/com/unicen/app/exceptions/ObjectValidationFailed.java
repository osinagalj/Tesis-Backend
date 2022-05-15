package com.unicen.app.exceptions;

import lombok.Getter;

/**
 * Deprecated. Use {@link CoreApiException} instead
 */
@Deprecated
@Getter
public class ObjectValidationFailed extends RuntimeException {
    private String apiResultMessage;

    public ObjectValidationFailed(Object object, String cause) {
        super("Object " + object + " not valid: " + cause);
        this.apiResultMessage = cause;
    }

    public ObjectValidationFailed(Object object, String apiResultMessage, String cause) {
        super("Object " + object + " not valid: " + cause);
        this.apiResultMessage = apiResultMessage;
    }

}
