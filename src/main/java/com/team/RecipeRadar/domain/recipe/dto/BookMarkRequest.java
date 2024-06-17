package com.team.RecipeRadar.domain.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "즐겨찾기 Request")
public class BookMarkRequest {

    private Long recipeId;
}
