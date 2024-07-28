package com.ecofy.core.dto;

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
public class ApiPageResultDTO<T> {
    private static final String OPERATION_SUCCESSFUL = "Operation successful";

    @NonNull
    private Integer totalElements = 0;

        @NonNull
        private String message;
        private Enum errorCode;
        boolean success;
        private T content;

        private ApiPageResultDTO(String message, T content) {
            this.errorCode = null;
            this.success = true;
            this.message = message;
            this.content = content;
        }

        private ApiPageResultDTO(String message, Enum errorCode) {
            this(message, errorCode, null, false);
        }

        private ApiPageResultDTO(String message, Enum errorCode, T content, boolean success) {
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
        public static ApiPageResultDTO<String> ofSuccess() {
            return new ApiPageResultDTO<>(OPERATION_SUCCESSFUL, "");
        }

    /**
     * Represents a successful operation with content
     *
     * @param content response object
     * @return ActionResult with generic successful message and the content
     */
    public static <T> ApiPageResultDTO<T> ofSuccess(T content) {
        return new ApiPageResultDTO<>(OPERATION_SUCCESSFUL, content);
    }




}
