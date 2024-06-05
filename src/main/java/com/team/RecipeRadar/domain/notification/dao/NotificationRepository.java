package com.team.RecipeRadar.domain.notification.dao;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> , CustomNotificationRepository{
    List<Notification> findByReceiver(Member member);       //알림 전제 조회

    boolean existsByReceiver_IdAndToName(Long memberId,String toName);
}
