package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;

import java.util.List;

public interface RecipeService {
    List<Recipe> searchRecipes(String query);

}
