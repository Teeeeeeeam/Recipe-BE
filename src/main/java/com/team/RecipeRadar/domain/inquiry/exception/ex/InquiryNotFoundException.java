package com.team.RecipeRadar.domain.inquiry.exception.ex;

public class InquiryNotFoundException extends RuntimeException{

    public InquiryNotFoundException (String message) {
        super(message);
    }
    public InquiryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
