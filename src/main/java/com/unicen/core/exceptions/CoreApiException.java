package com.unicen.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Main RuntimeException used to build a that will be returned to the client.
 * This exception is handled in ControllerExceptionHandler#handleCoreException to build the response.
 * <p>
 * The content of the ApiResultDTO / response is built using the values that were set on the instance of this class:
 * - errorCode: An enum that will be set to the variable <code>errorCode</code> in ApiResultDTO.
 * - apiResultMessage: String that will be set to the variable <code>message</code> in ApiResultDTO.
 * - httpStatus: The HTTP status code of the response.
 * - logError: A boolean indicating whether the exception should be logged to the console.
 * <p>
 * Additionally, we can pass an internal message using the constructor of this class, which will be logged to the console.
 * This is if we want to detail information to debug and not send it to the client.
 */
@Getter
public class CoreApiException extends RuntimeException {
    protected final Enum errorCode;
    protected final String apiResultMessage;
    protected final HttpStatus httpStatus;
    protected final boolean logError;

    protected CoreApiException(String apiResultMessage, HttpStatus httpStatus, Enum errorCode, boolean logError, Throwable e) {
        super(apiResultMessage, e);
        this.errorCode = errorCode;
        this.apiResultMessage = apiResultMessage;
        this.httpStatus = httpStatus;
        this.logError = logError;
    }

    protected CoreApiException(String apiResultMessage, HttpStatus httpStatus, Enum errorCode, boolean logError) {
        super(apiResultMessage);
        this.errorCode = errorCode;
        this.apiResultMessage = apiResultMessage;
        this.httpStatus = httpStatus;
        this.logError = logError;
    }

    protected CoreApiException(String internalMessage, String apiResultMessage, HttpStatus httpStatus, Enum errorCode, boolean logError) {
        super(internalMessage);
        this.errorCode = errorCode;
        this.apiResultMessage = apiResultMessage;
        this.httpStatus = httpStatus;
        this.logError = logError;
    }

    // 400 - Bad request
    public static CoreApiException validationError() {
        return validationError("Error validating the request", false);
    }

    public static CoreApiException validationError(String apiResultMessage) {
        return validationError(apiResultMessage, false);
    }

    public static CoreApiException validationError(String apiResultMessage, boolean logError) {
        return validationError(apiResultMessage, apiResultMessage, logError);
    }

    public static CoreApiException validationError(String internalMessage, String apiResultMessage, boolean logError) {
        return new CoreApiException(internalMessage, apiResultMessage, HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, logError);
    }

    public static CoreApiException objectAlreadyExists() {
        return new CoreApiException("Object already exists", HttpStatus.BAD_REQUEST, ErrorCode.OBJECT_ALREADY_EXISTS, false);
    }

    // 401 - Unauthorized
    public static CoreApiException insufficientPermissions() {
        return insufficientPermissions("User is not authorized to perform this action");
    }

    public static CoreApiException insufficientPermissions(String apiResultMessage) {
        return insufficientPermissions(apiResultMessage, false);
    }

    public static CoreApiException insufficientPermissions(String apiResultMessage, boolean logError) {
        return new CoreApiException(apiResultMessage, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, logError);
    }

    public static CoreApiException authenticationFailed(boolean logError) {
        return new CoreApiException("Authentication failed", HttpStatus.UNAUTHORIZED, ErrorCode.AUTHENTICATION_FAILED, logError);
    }

    // 404 - Not found
    public static CoreApiException objectNotFound() {
        return objectNotFound("Object not found");
    }

    public static CoreApiException objectNotFound(String apiResultMessage) {
        return objectNotFound(apiResultMessage, false);
    }

    public static CoreApiException objectNotFound(String apiResultMessage, boolean logError) {
        return new CoreApiException(apiResultMessage, HttpStatus.NOT_FOUND, ErrorCode.OBJECT_NOT_FOUND, logError);
    }

    public static CoreApiException objectNotFound(Class<?> objectClass, Object id) {
        return new CoreApiException("Object of type [" + objectClass + "] with id [" + id + "] not found", "Object not found", HttpStatus.NOT_FOUND, ErrorCode.OBJECT_NOT_FOUND, false);
    }

    public static CoreApiException resourceCannotBeLoaded(Class<?> objectClass, Object id) {
        return new CoreApiException("Object of type [" + objectClass + "] with id [" + id + "] not found", "Object not found", HttpStatus.NOT_FOUND, ErrorCode.OBJECT_NOT_FOUND, false);
    }

    public static CoreApiException resourceCannotBeLoaded(String path) {
        return new CoreApiException("Resource " + path + " can't be loaded", HttpStatus.NOT_FOUND, ErrorCode.OBJECT_NOT_FOUND, true);
    }

    public static CoreApiException objectNotFound(Class<?> objectClass, String property, Object id) {
        return new CoreApiException("Object of type [" + objectClass + "] with " + property + " [" + id + "] not found", "Object not found", HttpStatus.NOT_FOUND, ErrorCode.OBJECT_NOT_FOUND, false);
    }

    // 409 - Conflict
    public static CoreApiException conflictOfConditions() {
        return conflictOfConditions("The operation cannot be performed due to a conflict");
    }

    public static CoreApiException conflictOfConditions(String apiResultMessage) {
        return conflictOfConditions(apiResultMessage, false);
    }

    public static CoreApiException conflictOfConditions(String apiResultMessage, boolean logError) {
        return new CoreApiException(apiResultMessage, HttpStatus.CONFLICT, ErrorCode.CONFLICT, logError);
    }

    // 500 - Internal server error
    public static CoreApiException unexpectedError(Exception e) {
        return new CoreApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERIC_ERROR, true, e);
    }

}
