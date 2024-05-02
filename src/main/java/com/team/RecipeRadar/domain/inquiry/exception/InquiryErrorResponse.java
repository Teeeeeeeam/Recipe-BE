package com.team.RecipeRadar.domain.inquiry.exception;

import org.springframework.http.HttpStatus;

public class InquiryErrorResponse {

    private final HttpStatus status;
    private final String errorType;
    private final int code;
    private final String message;

    private InquiryErrorResponse(HttpStatus status, String errorType, int code, String message) {
        this.status = status;
        this.errorType = errorType;
        this.code = code;
        this.message = message;
    }

    public static InquiryErrorResponse from(HttpStatus status, String errorType, int code, String message) {
        return  new InquiryErrorResponse(status, errorType, code, message);
    }
}
