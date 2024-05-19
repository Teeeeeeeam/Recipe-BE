package com.team.RecipeRadar.domain.post.dto.user;

import com.team.RecipeRadar.domain.post.dto.PostDto;
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
