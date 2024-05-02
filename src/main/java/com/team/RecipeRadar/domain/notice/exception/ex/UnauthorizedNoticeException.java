package com.team.RecipeRadar.domain.notice.exception.ex;

public class UnauthorizedNoticeException extends RuntimeException{
    public UnauthorizedNoticeException(String message, Throwable cause) {
        super(message, cause);
    }
}
