package com.team.RecipeRadar.domain.email.event;

import lombok.Getter;

@Getter
public class QuestionMailEvent {
    private String email;

    public QuestionMailEvent(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
