package com.team.RecipeRadar.domain.notice.exception.ex;

public class AccessDeniedNoticeException extends  RuntimeException{
    public AccessDeniedNoticeException(String message, Throwable cause) {
        super(message, cause);
    }
}
