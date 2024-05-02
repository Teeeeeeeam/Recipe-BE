package com.team.RecipeRadar.domain.post.exception.ex;

public class InvalidPostRequestException extends RuntimeException{
    public InvalidPostRequestException (String message, Throwable cause) {
        super(message, cause);
    }
}
