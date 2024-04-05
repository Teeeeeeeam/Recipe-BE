package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dto.UserInfoPostLikeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

public interface LikeService<T> {

    Boolean addLike(T postLikeDto);
    Boolean checkLike(String JwtToken, Long postId);

    default UserInfoPostLikeResponse getUserLikesByPage(String jwtToken, String  loginId, Pageable pageable){
        return null;
    }


}
