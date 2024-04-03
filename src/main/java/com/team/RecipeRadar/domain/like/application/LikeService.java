package com.team.RecipeRadar.domain.like.application;

public interface LikeService<T> {

    Boolean addLike(T postLikeDto);
    Boolean checkLike(String JwtToken, Long postId);
}
