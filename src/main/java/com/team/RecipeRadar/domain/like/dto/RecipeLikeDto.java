package com.team.RecipeRadar.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeLikeDto {

    private Long memberId;
    private String recipeId;
}
