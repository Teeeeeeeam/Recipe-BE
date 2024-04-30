package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomRecipeRepository {

    Slice<RecipeDto> getRecipe(List<String> ingredient, Pageable pageable);


}
