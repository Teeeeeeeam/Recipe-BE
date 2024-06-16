package com.team.RecipeRadar.domain.Image.application;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;

public interface ImageService {

    void saveRecipeImg(Recipe recipe, UploadFile uploadFile);

    void delete_Recipe(Long recipeId);

}
