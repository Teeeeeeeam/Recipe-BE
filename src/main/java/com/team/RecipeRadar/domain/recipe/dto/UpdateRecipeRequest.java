package com.team.RecipeRadar.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateRecipeRequest {
    private String recipeTitle;
    private String recipeContent;
    private String recipeServing;
    private String cookingTime;
    private String ingredientsAmount;
    private String cookingStep;
    private String recipeLevel;
}
