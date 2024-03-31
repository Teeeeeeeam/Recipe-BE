package com.team.RecipeRadar.domain.recipe.dao;

import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RecipeRepositoryImplTest {

    @Autowired
    private RecipeRepositoryImpl recipeRepository;
    @Test
    @DisplayName("test")
    void test() {

    }

}