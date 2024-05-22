package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IngredientRepository extends JpaRepository<Ingredient,Long> , IngredientRepositoryCustom {

    @Modifying
    @Query("DELETE FROM  Ingredient i where  i.recipe.id=:recipeId")
    void deleteRecipeId(@Param("recipeId") Long recipe_Id);
}
