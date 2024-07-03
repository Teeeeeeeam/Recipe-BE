package com.team.RecipeRadar.domain.recipe.application.admin;

import com.team.RecipeRadar.domain.recipe.dto.request.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminRecipeService {

    long searchAllRecipes();

    void deleteRecipe(List<Long> ids);

    void saveRecipe(RecipeSaveRequest recipeSaveRequest, MultipartFile file);

    void updateRecipe(Long recipeId, RecipeUpdateRequest recipeUpdateRequest, MultipartFile file);
}
