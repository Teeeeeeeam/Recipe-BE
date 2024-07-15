package com.team.RecipeRadar.global.exception.ex.nosuch;

public enum NoSuchErrorType {

    NO_SUCH_MEMBER("사용자를 찾을 수 없습니다."),
    NO_SUCH_POST("게시글을 찾을 수 없습니다."),
    NO_SUCH_RECIPE("레시피를 찾을 수 없습니다."),
    NO_SUCH_IMAGE("이미지 파일을 찾을 수 없습니다."),
    NO_SUCH_EMAIL("해당 이메일을 찾을 수 없습니다."),
    NO_SUCH_NOTICE("해당 공지사항을 찾을 수 없습니다."),
    NO_SUCH_QUESTION("해당 문의사항을 찾을 수 없습니다."),
    NO_SUCH_DATA("해당 데이터를 찾을수 업습니다."),
    NO_SUCH_COMMENT("해당 댓글을 찾을 수 없습니다."),
    NO_SUCH_NOTIFICATION("해당 알림을 찾을 수 없습니다."),
    NO_SUCH_COOK_STEP("조리 순서를 찾을 수 없습니다.");

    private final String message;

    NoSuchErrorType(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
