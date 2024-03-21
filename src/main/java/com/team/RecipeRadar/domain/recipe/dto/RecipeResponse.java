package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.Getter;

@Getter
public class RecipeResponse {

    private final String recipeTitle;
    private final String recipeContent;
    private final String recipeServing;
    private final String cookingTime;
    private final String ingredientsAmount;
    private final String cookingStep;
    private final String recipeLevel;

    public RecipeResponse(Recipe recipe) {
        this.recipeTitle = recipe.getRecipeTitle();
        this.recipeContent = recipe.getRecipeContent();
        this.recipeServing = recipe.getRecipeServing();
        this.cookingTime = recipe.getCookingTime();
        this.ingredientsAmount = recipe.getIngredientsAmount();
        this.cookingStep = recipe.getCookingStep();
        this.recipeLevel = recipe.getRecipeLevel();
    }
}
