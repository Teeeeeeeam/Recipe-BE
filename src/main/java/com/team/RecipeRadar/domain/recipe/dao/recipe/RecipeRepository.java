package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String>{


    @Query("SELECT r.id FROM Ingredient i JOIN Recipe r ON r.id = i.recipe.id WHERE i.ingredients IS NULL OR i.ingredients = ''")
    List<String> getNoIngredients();
}
