package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import org.springframework.data.domain.Pageable;

public interface LikeService<T> {

    Boolean addLike(T postLikeDto);
    Boolean checkLike(String JwtToken, Long postId);

    UserInfoLikeResponse getUserLikesByPage(String jwtToken, String  loginId, Pageable pageable);

}
