package com.unicen.app.controller;


import com.unicen.app.dto.ApiResultDTO;
import com.unicen.app.dto.ErrorCode;
import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.exceptions.ObjectNotFoundException;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller for globally handle exceptions.
 * In this class we define:
 * - handlers for some predefined exceptions
 * - handler for {@link CoreApiException}
 * - Fallback handler to handle exceptions without a specific handler
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    // ------------- Fallback Handler -------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResultDTO<String>> fallbackHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResultDTO.ofError(ErrorCode.GENERIC_ERROR, "Internal server error"));
    }

    // ------------- Custom exceptions -------------
    @ExceptionHandler(CoreApiException.class)
    public ResponseEntity<ApiResultDTO<String>> handleCoreException(CoreApiException e) {
        if (e.isLogError()) {

        }
        return ResponseEntity.status(e.getHttpStatus()).body(ApiResultDTO.ofError(e.getErrorCode(), e.getApiResultMessage()));
    }
/*
    @ExceptionHandler(CoreApiException.class)
    public ResponseEntity<ApiResultDTO<String>> handleException(CoreApiException e) {
        Logger.error(this, e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResultDTO.ofError(ErrorCode.AUTHENTICATION_FAILED, "Authentication failed"));
    }
*/
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApiResultDTO<String>> handleException(ObjectNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResultDTO.ofError(ErrorCode.OBJECT_NOT_FOUND, "Not found"));
    }

    // ------------- Predefined exceptions -------------
    @ExceptionHandler(SizeException.class)
    public ResponseEntity<ApiResultDTO<String>> handleException(SizeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResultDTO.ofError(ErrorCode.INVALID_FILE_SIZE, "Invalid file size"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResultDTO<String>> handleException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResultDTO.ofError(ErrorCode.EMAIL_TAKEN, "Email taken"));
    }
}
