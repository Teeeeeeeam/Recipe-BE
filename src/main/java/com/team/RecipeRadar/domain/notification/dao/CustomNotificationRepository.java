package com.team.RecipeRadar.domain.notification.dao;

import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomNotificationRepository {

    Slice<NotificationDto> notificationPage(Long memberId , Pageable pageable, Long lastId);

    List<NotificationDto> notificationLimit(Long memberId);

    void deletePostLike(Long toId ,Long fromId,Long postId);

    void deleteComment(Long toId, Long fromId,Long commentId);
}
