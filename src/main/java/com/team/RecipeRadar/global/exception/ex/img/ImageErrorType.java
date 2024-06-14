package com.team.RecipeRadar.global.exception.ex.img;

public enum ImageErrorType {
    MISSING_PRIMARY_IMAGE("대표 이미지를 등록해주세요"),
    INVALID_IMAGE_FORMAT("이미지 형식이 잘못되었습니다"),
    IMAGE_TOO_LARGE("이미지 크기가 너무 큽니다"),
    UPLOAD_FAILS("업로드를 실패했습니다.");

    private final String message;

    ImageErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
