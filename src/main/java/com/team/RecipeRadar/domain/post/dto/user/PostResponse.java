package com.team.RecipeRadar.domain.post.dto;

import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponse {

    private boolean nextPage;
    private List<PostDto> posts;

    public PostResponse(boolean nextPage, List<PostDto> posts) {
        this.nextPage = nextPage;
        this.posts = posts;
    }
}
