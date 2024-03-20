package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecipeDto {

    private String recipeTitle;
    private String recipeContent;

    public Recipe toEntity() {
        return Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .build();
    }
    public void RecipeResponse(Recipe recipe) {
        this.recipeTitle = recipe.getRecipeTitle();
        this.recipeContent = recipe.getRecipeContent();
    }
}
