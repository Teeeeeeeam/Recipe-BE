package com.team.RecipeRadar.domain.comment.dto;

import com.team.RecipeRadar.domain.comment.domain.Comment;

public class AddCommentRequest {

    private String commentContent;

    public Comment toEntity() {
        return Comment.builder()
                .commentContent(commentContent)
                .build();
    }
}