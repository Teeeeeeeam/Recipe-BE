package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.api.user.OrderType;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomRecipeRepository {

    Slice<RecipeDto> getRecipe(List<String> ingredient, Long lastRecipeId,Pageable pageable);

    Slice<RecipeDto> adminSearchTitleOrIng(List<String> ingredient,String title,Long lastRecipeId,Pageable pageable);

    Slice<RecipeDto> userSearchRecipe(List<String> ingredient, List<CookIngredients> cookIngredients,List<CookMethods> cookMethods,List<DishTypes> dishTypes,String title,
                                      OrderType order, Integer likeCount, Long lastId,Pageable pageable);

    RecipeDto getRecipeDetails(Long recipeId);

    List<RecipeDto> mainPageRecipe();

    Slice<RecipeDto> searchCategory(List<CookIngredients> ingredients, List<CookMethods> cookMethods, List<DishTypes> dishTypes, OrderType order,Integer count, Long lastId, Pageable pageable);
}
