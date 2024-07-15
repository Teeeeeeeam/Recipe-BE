package com.team.RecipeRadar.global.exception.ex.img;

public class ImageException extends RuntimeException{

    private final ImageErrorType errorType;

    public ImageException(ImageErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ImageErrorType getErrorType() {
        return errorType;
    }

}
