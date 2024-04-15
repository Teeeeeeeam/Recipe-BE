package com.team.RecipeRadar.domain.inquiry.exception;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;

public class InquiryException extends RuntimeException {

    public InquiryException() {
        super();
    }

    public InquiryException(String message) {
        super(message);
    }

    public InquiryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InquiryException(Throwable cause) {
        super(cause);
    }

    protected InquiryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
