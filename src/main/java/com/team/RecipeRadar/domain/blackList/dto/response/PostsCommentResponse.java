package com.team.RecipeRadar.domain.blackList.dto.response;

import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostsCommentResponse {

    private Boolean nextPage;
    private List<CommentDto> comment;
}
