package com.team.RecipeRadar.domain.notification.dao;

import com.team.RecipeRadar.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> , CustomNotificationRepository{
}
