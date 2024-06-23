package com.team.RecipeRadar.domain.notification.dto.response;

import com.team.RecipeRadar.domain.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String content;
    private String url;

    @Builder
    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
    }

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(notification);
    }
}
