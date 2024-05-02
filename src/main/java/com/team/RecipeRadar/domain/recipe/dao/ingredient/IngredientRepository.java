package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient,Long> , IngredientRepositoryCustom {
}
