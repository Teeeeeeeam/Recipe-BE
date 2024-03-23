package com.team.RecipeRadar.domain.like.ex;

public class LikeException extends RuntimeException{
    public LikeException() {
        super();
    }

    public LikeException(String message) {
        super(message);
    }

    public LikeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LikeException(Throwable cause) {
        super(cause);
    }

    protected LikeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
