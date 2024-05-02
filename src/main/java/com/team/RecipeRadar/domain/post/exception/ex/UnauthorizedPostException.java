package com.team.RecipeRadar.domain.post.exception.ex;

public class UnauthorizedPostException extends RuntimeException {
    public  UnauthorizedPostException(String message, Throwable cause) {
        super(message, cause);
    }
}
