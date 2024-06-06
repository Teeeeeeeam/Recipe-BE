package com.team.RecipeRadar.domain.answer.exception;

public class AnswerException extends RuntimeException{

    public AnswerException() {
        super();
    }

    public AnswerException(String message) {
        super(message);
    }

    public AnswerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnswerException(Throwable cause) {
        super(cause);
    }

    protected AnswerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
