package com.team.RecipeRadar.domain.post.dto.response;

import com.team.RecipeRadar.domain.post.dto.PostDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Schema(name = "게시글 페이징 Response")
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private boolean nextPage;
    private List<PostDto> posts;

}
