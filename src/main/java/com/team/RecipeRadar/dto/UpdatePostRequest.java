package com.team.RecipeRadar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdatePostRequest {
    private String postTitle;
    private String postContent;
    private String postServing;
    private String postCookingTime;
    private String postCookingLevel;
}
