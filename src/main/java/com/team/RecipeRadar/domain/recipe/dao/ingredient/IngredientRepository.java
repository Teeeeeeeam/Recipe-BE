package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> , IngredientRepositoryCustom {

    Ingredient findByRecipe_Id(String recipeId);
}
