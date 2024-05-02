package com.team.RecipeRadar.domain.comment.dto;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import lombok.Getter;

@Getter
public class CommentResponse {
    private final String commentContent;

    public CommentResponse(Comment comment) {
        this.commentContent = comment.getCommentContent();
    }
}