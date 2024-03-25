package com.team.RecipeRadar.domain.notice.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private final HttpStatus status;
    private final String errorType;
    private final int code;
    private final String message;

    private ErrorResponse(HttpStatus status, String errorType, int code, String message) {
        this.status = status;
        this.errorType = errorType;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse from(HttpStatus status, String errorType, int code, String message) {
        return new ErrorResponse(status, errorType, code, message);
    }
}
