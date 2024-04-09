package com.team.RecipeRadar.domain.like.dto;

import com.team.RecipeRadar.domain.like.domain.PostLike;
import lombok.Data;

@Data
public class UserLikeDto {

    private Long id;
    private String content;
    private String title;

    public UserLikeDto(Long id, String content, String title) {
        this.id = id;
        this.content = content;
        this.title = title;
    }

    public static UserLikeDto of(PostLike postLike){
        return new UserLikeDto(postLike.getId(),postLike.getPost().getPostContent(),postLike.getPost().getPostTitle());
    }
}
