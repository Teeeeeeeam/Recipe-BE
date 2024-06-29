package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslConfig.class)
@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class IngredientRepositoryTest {

    @Autowired IngredientRepository ingredientRepository;
    @Autowired RecipeRepository recipeRepository;

    private Recipe recipe;
    private Ingredient ingredient;

    @BeforeEach
    void Setup(){
        recipe = Recipe.builder().title("레시피1").build();
        recipeRepository.save(recipe);

        ingredient = Ingredient.builder().ingredients("김치|밥|고기").recipe(recipe).build();
        ingredientRepository.save(ingredient);
    }

    
    @Test
    @DisplayName("레시피 아이디로 재료 삭제")
    void deleteByIng(){
        List<Ingredient> before = ingredientRepository.findAll();

        ingredientRepository.deleteRecipeId(recipe.getId());
        List<Ingredient> after = ingredientRepository.findAll();

        assertThat(before).hasSize(1);
        assertThat(after).hasSize(0);
    }
}