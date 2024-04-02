package com.team.RecipeRadar.domain.notice.exception;

import org.springframework.http.HttpStatus;

public class NoticeErrorResponse {
    private final HttpStatus status;
    private final String errorType;
    private final int code;
    private final String message;

    private NoticeErrorResponse(HttpStatus status, String errorType, int code, String message) {
        this.status = status;
        this.errorType = errorType;
        this.code = code;
        this.message = message;
    }

    public static NoticeErrorResponse from(HttpStatus status, String errorType, int code, String message) {
        return new NoticeErrorResponse(status, errorType, code, message);
    }
}
