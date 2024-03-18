package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Comment;

public class AddCommentRequest {

    private String commentContent;

    public Comment toEntity() {
        return Comment.builder()
                .commentContent(commentContent)
                .build();
    }
}
