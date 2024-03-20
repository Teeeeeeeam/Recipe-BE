package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Recipe;
import com.team.RecipeRadar.dto.AddRecipeRequest;
import com.team.RecipeRadar.dto.UpdateRecipeRequest;
import java.util.List;

public interface RecipeService {
    Recipe save(AddRecipeRequest request);

    List<Recipe> findAll();

    Recipe findById(long id);

    void delete(long id);

    Recipe update(long id, UpdateRecipeRequest request);

    List<Recipe> searchRecipes(String query);

    long getRecipeCount();
}
