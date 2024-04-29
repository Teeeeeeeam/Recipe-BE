package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dto.DetailRecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.SearchRecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    default void saveRecipeData(){}

    Page<SearchRecipeResponse> searchRecipe(List<String> ingredients, Pageable pageable);

    DetailRecipeResponse detailRecipeInfo(String recipeId);


}

