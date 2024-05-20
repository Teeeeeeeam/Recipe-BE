package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CookStepRepository extends JpaRepository<CookingStep, Long> {

    @Modifying
    @Query("DELETE from CookingStep c where c.recipe.id=:recipeId")
    void deleteRecipeId(@Param("recipeId") Long recipe_Id);
}
