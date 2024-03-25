package com.team.RecipeRadar.domain.notice.exception.ex;

public class NoticeNotFoundException extends RuntimeException{
    public NoticeNotFoundException(String message){
        super(message);
    }
}
