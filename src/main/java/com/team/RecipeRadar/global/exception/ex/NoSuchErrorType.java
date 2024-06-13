package com.team.RecipeRadar.global.exception.ex;

public enum NoSuchErrorType {

    NO_SUCH_MEMBER("사용자를 찾을수 업습니다."),
    NO_SUCH_POST("게시글을 찾을 수 없습니다.");

    private final String message;

    NoSuchErrorType(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
