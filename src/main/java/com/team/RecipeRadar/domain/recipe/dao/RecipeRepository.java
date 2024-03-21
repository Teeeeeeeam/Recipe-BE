package com.team.RecipeRadar.domain.recipe.dao;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByRecipeTitleContainingIgnoreCase(String recipeTitle);

    long countByRecipeTitleIsNotNull();
}
