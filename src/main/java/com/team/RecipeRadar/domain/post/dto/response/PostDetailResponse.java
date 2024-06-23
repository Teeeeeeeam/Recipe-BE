package com.team.RecipeRadar.domain.post.dto.response;

import com.team.RecipeRadar.domain.post.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private PostDto post;
}
