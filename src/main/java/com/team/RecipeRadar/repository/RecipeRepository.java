package com.team.RecipeRadar.repository;

import com.team.RecipeRadar.Entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByRecipeTitleContainingIgnoreCase(String recipeTitle);

    long countByRecipeTitleIsNotNull();
}
