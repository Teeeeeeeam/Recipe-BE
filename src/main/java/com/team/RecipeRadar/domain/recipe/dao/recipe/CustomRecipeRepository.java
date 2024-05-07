package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomRecipeRepository {

    Slice<RecipeDto> getRecipe(List<String> ingredient, Long lastRecipeId,Pageable pageable);

    Page<RecipeDto> getNormalPage(List<String> ingredient,Pageable pageable);

    RecipeDto getRecipeDetails(Long recipeId);

    List<RecipeDto> mainPageRecipe();

}
