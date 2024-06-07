package com.team.RecipeRadar.domain.like.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLikeDto {

    private Long id;
    private String content;
    private String title;

    public UserLikeDto(Long id, String content, String title) {
        this.id = id;
        this.content = content;
        this.title = title;
    }

    public UserLikeDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static UserLikeDto Post_of(PostLike postLike){
        return new UserLikeDto(postLike.getId(),postLike.getPost().getPostContent(),postLike.getPost().getPostTitle());
    }
    public static UserLikeDto Recipe_of(RecipeLike recipeLike){
        return new UserLikeDto(recipeLike.getId(),recipeLike.getRecipe().getTitle());
    }
}
