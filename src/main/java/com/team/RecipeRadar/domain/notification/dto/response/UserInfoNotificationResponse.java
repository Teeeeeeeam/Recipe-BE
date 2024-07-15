package com.team.RecipeRadar.domain.notification.dto.response;

import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoNotificationResponse {

    Boolean hasNext;
    List<NotificationDto> notification;
}
