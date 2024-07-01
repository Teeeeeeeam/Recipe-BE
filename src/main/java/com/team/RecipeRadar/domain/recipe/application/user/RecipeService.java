package com.team.RecipeRadar.domain.recipe.application.user;


import com.team.RecipeRadar.domain.recipe.api.user.OrderType;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    RecipeResponse searchRecipesByIngredients(List<String> ingredients, Long lastRecipeId, Pageable pageable);

    RecipeSearchResponse searchRecipeByIngredientsNormal(List<String> ingredients, List<CookIngredients> cookIngredients, List<CookMethods> cookMethods, List<DishTypes> dishTypes, String title,
                                                         OrderType order, Integer likeCount, Long lastId, Pageable pageable);

    RecipeDetailsResponse getRecipeDetails(Long recipeId);

    MainPageRecipeResponse mainPageRecipe();
}
