package com.ecofy.core.exceptions;

/**
 * List of potential errors returned by the API
 */
public enum ErrorCode {
    BAD_REQUEST,
    UNAUTHORIZED,
    CONFLICT,
    GENERIC_ERROR,
    AUTHENTICATION_FAILED,
    OBJECT_NOT_FOUND,
    INVALID_FILE_SIZE,
    EMAIL_TAKEN,
    OBJECT_ALREADY_EXISTS,
    VALIDATION_ERROR
}
