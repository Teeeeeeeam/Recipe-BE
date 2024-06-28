package com.team.RecipeRadar.domain.Image.application;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;

public interface ImageService {

    void saveRecipeImage(Recipe recipe, UploadFile uploadFile);

    void deleteRecipe(Long recipeId);

}
