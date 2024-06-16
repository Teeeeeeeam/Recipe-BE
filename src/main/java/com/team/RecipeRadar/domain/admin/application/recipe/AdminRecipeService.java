package com.team.RecipeRadar.domain.admin.application.recipe;

import com.team.RecipeRadar.domain.recipe.dto.RecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.RecipeUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminRecipeService {

    long searchAllRecipes();

    void deleteRecipe(List<Long> ids);

    RecipeResponse searchRecipesByTitleAndIngredients(List<String> ingredients, String title, Long lastRecipeId, Pageable pageable);

    void saveRecipe(RecipeSaveRequest recipeSaveRequest, MultipartFile file);

    void updateRecipe(Long recipeId, RecipeUpdateRequest recipeUpdateRequest, MultipartFile file);
}
