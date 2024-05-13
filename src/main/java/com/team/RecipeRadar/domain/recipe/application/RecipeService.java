package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {

    RecipeResponse searchRecipesByIngredients(List<String> ingredients, Long lastRecipeId,Pageable pageable);

    RecipeResponse searchRecipesByTitleAndIngredients(List<String> ingredients, String title,Long lastRecipeId,Pageable pageable);

    Page<RecipeDto> searchRecipeByIngredientsNormal(List<String> ingredients, Pageable pageable);

    RecipeDetailsResponse getRecipeDetails(Long recipeId);

    MainPageRecipeResponse mainPageRecipe();

    Recipe saveRecipe(RecipeSaveRequest recipeSaveRequest);
    void updateRecipe(Long recipeId,  RecipeUpdateRequest recipeUpdateRequest, MultipartFile file) throws Exception;

}
