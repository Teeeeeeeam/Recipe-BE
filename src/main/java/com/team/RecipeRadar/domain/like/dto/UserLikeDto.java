package com.team.RecipeRadar.domain.like.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLikeDto {

    private Long like_id;
    private Long content_id;
    private String content;
    private String title;

    public UserLikeDto(Long id,Long content_id, String content, String title) {
        this.like_id = id;
        this.content_id=content_id;
        this.content = content;
        this.title = title;
    }

    public UserLikeDto(Long id,Long content_id, String title) {
        this.like_id = id;
        this.content_id=content_id;
        this.title = title;
    }

    public static UserLikeDto Post_of(PostLike postLike){
        return new UserLikeDto(postLike.getId(),postLike.getPost().getId(),postLike.getPost().getPostContent(),postLike.getPost().getPostTitle());
    }
    public static UserLikeDto Recipe_of(RecipeLike recipeLike){
        return new UserLikeDto(recipeLike.getId(),recipeLike.getRecipe().getId(),recipeLike.getRecipe().getTitle());
    }
}
