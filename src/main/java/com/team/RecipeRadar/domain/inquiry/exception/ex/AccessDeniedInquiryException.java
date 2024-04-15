package com.team.RecipeRadar.domain.inquiry.exception.ex;

public class AccessDeniedInquiryException extends RuntimeException {
    public AccessDeniedInquiryException(String message, Throwable cause) {
        super(message, cause);
    }
}
