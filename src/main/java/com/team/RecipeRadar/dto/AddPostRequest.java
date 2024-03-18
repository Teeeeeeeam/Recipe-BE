package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddPostRequest {

    private String postTitle;
    private String postContent;
    private String postServing;
    private String postCookingTime;
    private String postCookingLevel;

    public Post toEntity() {
        return Post.builder()
                .postTitle(postTitle)
                .postContent(postContent)
                .postServing(postServing)
                .postCookingTime(postCookingTime)
                .postCookingLevel(postCookingLevel)
                .build();
    }
}
