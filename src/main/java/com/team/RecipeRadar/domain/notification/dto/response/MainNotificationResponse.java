package com.team.RecipeRadar.domain.notification.dto.response;

import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import lombok.Data;

import java.util.List;

@Data
public class MainNotificationResponse {

    public List<NotificationDto> notification;

    private MainNotificationResponse(List<NotificationDto> notification) {
        this.notification = notification;
    }

    public static MainNotificationResponse of(List<NotificationDto> notificationDtoList){
        return new MainNotificationResponse(notificationDtoList);
    }
}
