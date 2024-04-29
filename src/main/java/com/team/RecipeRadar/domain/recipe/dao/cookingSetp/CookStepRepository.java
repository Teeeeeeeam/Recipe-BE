package com.team.RecipeRadar.domain.recipe.dao.cookingSetp;

import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CookStepRepository extends JpaRepository<CookingStep, Long> {
}
