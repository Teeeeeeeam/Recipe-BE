package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dto.response.UserInfoLikeResponse;
import org.springframework.data.domain.Pageable;

public interface LikeService<T> {

    Boolean addLike(T postLikeDto,Long memberId);
    Boolean checkLike(Long memberId,Long postId);

    UserInfoLikeResponse getUserLikesByPage(Long memberId, Long Like_lastId,Pageable pageable);
}
