package com.team.RecipeRadar.global.exception.ex;

public class ForbiddenException extends RuntimeException{

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
