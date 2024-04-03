package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddRecipeRequest {

    private String recipeTitle;
    private String recipeContent;
    private String recipeServing;
    private String cookingTime;
    private String ingredientsAmount;
    private String cookingStep;
    private String recipeLevel;

    public Recipe toEntity() {
        return Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .recipeServing(recipeServing)
                .cookingTime(cookingTime)
                .ingredientsAmount(ingredientsAmount)
                .cookingStep(cookingStep)
                .recipeLevel(recipeLevel)
                .likeCount(0)
                .build();
    }
}
