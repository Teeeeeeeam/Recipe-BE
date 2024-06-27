package com.team.RecipeRadar.domain.post.dto.response;

import com.team.RecipeRadar.domain.post.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeLikeTopPostResponse {

    private List<PostDto> post;
}
