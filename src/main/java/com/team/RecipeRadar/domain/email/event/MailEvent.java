package com.team.RecipeRadar.domain.email.event;

import lombok.Getter;

@Getter
public class MailEvent {

    private String email;

    public MailEvent(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
