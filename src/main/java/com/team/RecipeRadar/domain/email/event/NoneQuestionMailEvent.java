package com.team.RecipeRadar.domain.email.event;

import lombok.Getter;

@Getter
public class NoneQuestionMailEvent {
    private String email;
    private String subject;
    private String body;

    public NoneQuestionMailEvent(String email, String subject, String body){
        this.email = email;
        this.subject=subject;
        this.body = body;
    }

}
