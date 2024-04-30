package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired RecipeRepository recipeRepository;
    @Autowired IngredientRepository ingredientRepository;
    
    @Test
    @DisplayName("무한 페이징(Slice) 테스트 ")
    void findIng(){
        List<Recipe> recipes = new ArrayList<>();
        for (int i=1;i<=5;i++) {
             recipes.add(Recipe.builder().id((long) i).title("제목"+i).cookingTime("시간"+i).build());      // id value = [0, 1, 2, 3, 4]
        }

        recipeRepository.saveAll(recipes);


        Ingredient ingredient = Ingredient.builder().recipe(recipeRepository.findById(1l).get()).ingredients("밥").build();
        Ingredient ingredient1 = Ingredient.builder().recipe(recipeRepository.findById(2l).get()).ingredients("밥|고기").build();
        Ingredient ingredient2 = Ingredient.builder().recipe(recipeRepository.findById(3l).get()).ingredients("밥|김치").build();
        Ingredient ingredient3 = Ingredient.builder().recipe(recipeRepository.findById(4l).get()).ingredients("밥|돼지고기|밑반찬").build();
        Ingredient ingredient4 = Ingredient.builder().recipe(recipeRepository.findById(5l).get()).ingredients("밥|물김치|닭고기").build();

        ingredientRepository.save(ingredient);
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        ingredientRepository.save(ingredient4);


        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Pageable pageRequest_nextPageTrue = PageRequest.of(0, 2);

        Slice<RecipeDto> recipe_FirstPage = recipeRepository.getRecipe(ingredients, pageRequest_nextPageTrue);

        List<RecipeDto> content = recipe_FirstPage.getContent();
        assertThat(content.get(0).getId()).isEqualTo(1l);
        assertThat(recipe_FirstPage.hasNext()).isTrue();


        Pageable pageRequest_nextPageFail = PageRequest.of(2, 2);

        Slice<RecipeDto> recipe_lastPage = recipeRepository.getRecipe(ingredients, pageRequest_nextPageFail);
        assertThat(recipe_lastPage.getContent().get(0).getId()).isEqualTo(5l);
        assertThat(recipe_lastPage.hasNext()).isFalse();
    }

}