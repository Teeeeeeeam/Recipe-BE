package com.team.RecipeRadar.domain.post.dto.user;

import com.team.RecipeRadar.domain.post.dto.PostDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "게시글 페이징 Response")
public class PostResponse {

    private boolean nextPage;
    private List<PostDto> posts;

    public PostResponse(boolean nextPage, List<PostDto> posts) {
        this.nextPage = nextPage;
        this.posts = posts;
    }
}
