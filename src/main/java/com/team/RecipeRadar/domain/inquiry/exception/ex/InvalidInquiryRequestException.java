package com.team.RecipeRadar.domain.inquiry.exception.ex;

public class InvalidInquiryRequestException extends RuntimeException {
    public InvalidInquiryRequestException (String message, Throwable cause) {
        super(message, cause);
    }
}
