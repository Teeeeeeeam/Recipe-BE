package com.team.RecipeRadar.domain.post.dto.user;

import lombok.Data;

@Data
public class ValidPostRequest {

    private String password;
    private Long postId;
}
