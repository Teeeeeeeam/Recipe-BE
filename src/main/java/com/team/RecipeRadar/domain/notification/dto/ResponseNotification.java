package com.team.RecipeRadar.domain.notification.dto;

import com.team.RecipeRadar.domain.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseNotification {

    private Long id;
    private String content;
    private String url;

    @Builder
    public ResponseNotification(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
    }

    public static ResponseNotification from(Notification notification) {
        return new ResponseNotification(notification);
    }
}
