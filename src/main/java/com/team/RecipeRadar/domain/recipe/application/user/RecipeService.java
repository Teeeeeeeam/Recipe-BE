package com.team.RecipeRadar.domain.recipe.application.user;


import com.team.RecipeRadar.domain.recipe.dto.response.MainPageRecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeDetailsResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeNormalPageResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    RecipeResponse searchRecipesByIngredients(List<String> ingredients, Long lastRecipeId, Pageable pageable);

    RecipeNormalPageResponse searchRecipeByIngredientsNormal(List<String> ingredients, String title, Pageable pageable);

    RecipeDetailsResponse getRecipeDetails(Long recipeId);

    MainPageRecipeResponse mainPageRecipe();

}
