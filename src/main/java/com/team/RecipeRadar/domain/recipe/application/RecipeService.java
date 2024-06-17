package com.team.RecipeRadar.domain.recipe.application;


import com.team.RecipeRadar.domain.recipe.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    RecipeResponse searchRecipesByIngredients(List<String> ingredients, Long lastRecipeId,Pageable pageable);

    RecipeNormalPageResponse searchRecipeByIngredientsNormal(List<String> ingredients,String title,Pageable pageable);

    RecipeDetailsResponse getRecipeDetails(Long recipeId);

    MainPageRecipeResponse mainPageRecipe();

}
