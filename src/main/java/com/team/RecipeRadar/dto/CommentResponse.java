package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponse {
    private final String commentContent;

    public CommentResponse(Comment comment) {
        this.commentContent = comment.getCommentContent();
    }
}
