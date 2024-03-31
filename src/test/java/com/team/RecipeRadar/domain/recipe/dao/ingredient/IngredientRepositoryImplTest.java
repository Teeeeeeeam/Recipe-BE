package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.querydsl.core.Tuple;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepositoryImpl;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class IngredientRepositoryImplTest {

    @Autowired
    RecipeRepositoryImpl recipeRepository;

    @Test
    @DisplayName("findRecipeByIngredient 재료 동적 으로 조회 테스트")
    void test() {
        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<String> ingredientTitles = List.of("김치");

        Page<Recipe> recipeByIngredient = recipeRepository.findRecipeByIngredient(ingredientTitles, pageable);

        recipeByIngredient.forEach(System.out::println);

    }
}