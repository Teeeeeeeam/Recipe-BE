package com.team.RecipeRadar.domain.notice.exception.ex;

public class InvalidNoticeRequestException extends RuntimeException {
    public InvalidNoticeRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
