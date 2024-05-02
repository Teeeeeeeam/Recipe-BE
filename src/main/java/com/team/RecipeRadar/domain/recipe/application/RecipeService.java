package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dto.MainPageRecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDetailsResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    RecipeResponse searchRecipesByIngredients(List<String> ingredients, Long lastRecipeId,Pageable pageable);

     Page<RecipeDto> searchRecipeByIngredientsNormal(List<String> ingredients, Pageable pageable);

    RecipeDetailsResponse getRecipeDetails(Long recipeId);

    MainPageRecipeResponse mainPageRecipe();
}
