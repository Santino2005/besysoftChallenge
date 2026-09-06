package com.besysoft.common.errorHandler;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorCode,
        String message,
        String field,
        LocalDateTime timestamp
) {
    public ErrorResponse(String errorCode, String message, String field) {
        this(errorCode, message, field, LocalDateTime.now());
    }

    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, null, LocalDateTime.now());
    }

    public static ErrorResponse response(String errorCode, String message, String field) {
        return new ErrorResponse(errorCode, message, field);
    }

    public static ErrorResponse response(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }

    public static ErrorResponse of(String errorCode, String message, String field) {
        return new ErrorResponse(errorCode, message, field);
    }

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }
}
