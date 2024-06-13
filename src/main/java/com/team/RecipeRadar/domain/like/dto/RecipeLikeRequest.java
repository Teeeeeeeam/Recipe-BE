package com.team.RecipeRadar.domain.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "레시피 좋아요 Request")
public class RecipeLikeRequest {

    private Long recipeId;
}
