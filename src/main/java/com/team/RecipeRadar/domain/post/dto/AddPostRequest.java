package com.team.RecipeRadar.domain.post.dto;

import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AddPostRequest {

    private Long id;
    private String postTitle;
    private String postContent;
    private String postServing;
    private String postCookingTime;
    private String postCookingLevel;
    private Integer postLikeCount;

    public Post toEntity() {
        return Post.builder()
                .id(id)
                .postTitle(postTitle)
                .postContent(postContent)
                .postServing(postServing)
                .postCookingTime(postCookingTime)
                .postCookingLevel(postCookingLevel)
                .postLikeCount(postLikeCount)
                .build();
    }
}
