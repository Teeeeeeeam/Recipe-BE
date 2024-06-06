package com.team.RecipeRadar.global.email.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ResignMemberEvent {

    private String email;

    public ResignMemberEvent(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
