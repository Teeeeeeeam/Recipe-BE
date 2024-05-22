package com.team.RecipeRadar.domain.post.dto.user;

import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponse {

    private PostDto post;

    private List<CommentDto> comments;
}
