package com.unicen.app.dto;

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
    GEOLOCATION,
    OBJECT_ALREADY_EXISTS,
    VALIDATION_ERROR
}
