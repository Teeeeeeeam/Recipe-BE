package com.team.RecipeRadar.global.exception.ex.nosuch;

public class NoSuchDataException extends RuntimeException{

    private final NoSuchErrorType noSuchErrorType;

    public NoSuchDataException(NoSuchErrorType noSuchErrorType) {
        super(noSuchErrorType.getMessage());
        this.noSuchErrorType = noSuchErrorType;
    }
}
