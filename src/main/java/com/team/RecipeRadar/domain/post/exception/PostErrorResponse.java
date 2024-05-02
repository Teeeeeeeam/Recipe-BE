package com.team.RecipeRadar.domain.post.exception;

import org.springframework.http.HttpStatus;

public class PostErrorResponse {
    private final HttpStatus status;
    private final String errorType;
    private final int code;
    private final String message;

    private PostErrorResponse(HttpStatus status, String errorType, int code, String message) {
        this.status = status;
        this.errorType = errorType;
        this.code = code;
        this.message = message;
    }

    public static PostErrorResponse from(HttpStatus status, String errorType, int code, String message) {
        return new PostErrorResponse(status, errorType, code, message);
    }
}