package com.team.RecipeRadar.domain.like.postLike.application;

import com.team.RecipeRadar.domain.like.postLike.domain.PostLike;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;

public interface PostLikeService {

    Boolean addLike(PostLikeDto postLikeDto);
    Boolean checkLike(String JwtToken, Long postId);
}
