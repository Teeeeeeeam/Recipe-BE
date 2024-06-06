package com.team.RecipeRadar.domain.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class NotificationContent {

    private String content;

    public NotificationContent(String content) {
        this.content = content;
    }
}
