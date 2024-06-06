package com.team.RecipeRadar.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserInfoNotification {

    Boolean hasNext;
    List<NotificationDto> notification;
}
