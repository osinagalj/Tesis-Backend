package com.unicen.core.dto;

import com.unicen.core.exceptions.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Generalizes response for all API methods
 *
 * @param <T> the type of object wrapped by the response
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiResultDTO<T> {
    private static final String OPERATION_SUCCESSFUL = "Operation successful";

    @NonNull
    private String message;
    private Enum errorCode;
    boolean success;
    private T content;

    private ApiResultDTO(String message, T content) {
        this.errorCode = null;
        this.success = true;
        this.message = message;
        this.content = content;
    }

    private ApiResultDTO(String message, Enum errorCode) {
        this(message, errorCode, null, false);
    }

    private ApiResultDTO(String message, Enum errorCode, T content, boolean success) {
        this.message = message;
        this.errorCode = errorCode;
        this.content = content;
        this.success = success;
    }

    /**
     * Represents a successful operation without a content.
     *
     * @return ActionResult with a success message but null content
     */
    public static ApiResultDTO<String> ofSuccess() {
        return new ApiResultDTO<>(OPERATION_SUCCESSFUL, "");
    }

    /**
     * Represents a successful operation with content
     *
     * @param content response object
     * @return ActionResult with generic successful message and the content
     */
    public static <T> ApiResultDTO<T> ofSuccess(T content) {
        return new ApiResultDTO<>(OPERATION_SUCCESSFUL, content);
    }

    /**
     * Represents a successful operation with content and a message
     *
     * @param content response object
     * @param message detailing operation
     * @return ActionResult with both content and message included
     */
    public static <T> ApiResultDTO<T> ofSuccess(T content, String message) {
        return new ApiResultDTO<>(message, content);
    }

    /**
     * Represents a failed operation with a message
     *
     * @param message detailing operation
     * @return ActionResult with success as false and a generic error code
     */
    public static <T> ApiResultDTO<T> ofError(String message) {
        return new ApiResultDTO<>(message, ErrorCode.GENERIC_ERROR);
    }

    /**
     * Represents a failed operation with an error code and a message
     *
     * @param reason  Error code that represents the error that caused the operation
     *                to fail
     * @param message detailing operation
     * @return ActionResult with success as false and the error code that was passed
     */
    public static <T> ApiResultDTO<T> ofError(Enum reason, String message) {
        return new ApiResultDTO<>(message, reason);
    }
}
