package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;

import java.util.List;

public interface IngredientRepositoryCustom {


    List<Recipe> searchRecipeByIngredient();

    void updateRecipe_ing(Long recipe_id,String ings);

}
