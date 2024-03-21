package com.team.RecipeRadar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.AddRecipeRequest;
import com.team.RecipeRadar.domain.recipe.dto.UpdateRecipeRequest;
import com.team.RecipeRadar.domain.recipe.dao.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired // 직렬화, 역직렬화를 위한 클래스
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    RecipeRepository recipeRepository;

    @BeforeEach // 테스트 실행전 실행하는 메서드
    public void setMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).
                build();
        recipeRepository.deleteAll();

    }

    @DisplayName("addRecipe: 레시피 추가에 성공한다.")
    @Test
    public void addRecipe() throws Exception {
        //given
        final String url = "/api/admin/recipes";
        final String recipeTitle = "recipeTitle";
        final String recipeContent = "recipeContent";
        final String recipeServing = "recipeServing";
        final String cookingTime = "cookingTime";
        final String ingredientsAmount = "ingredientsAmount";
        final String cookingStep = "cookingStep";
        final String recipeLevel = "recipeLevel";
        final AddRecipeRequest userRequest = new AddRecipeRequest(recipeTitle, recipeContent, recipeServing, cookingTime, ingredientsAmount, cookingStep, recipeLevel);

        //객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        //when
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then
        result.andExpect(status().isCreated());

        List<Recipe> recipes = recipeRepository.findAll();

        assertThat(recipes.size()).isEqualTo(1); //크기가 1인지 검증
        assertThat(recipes.get(0).getRecipeTitle()).isEqualTo(recipeTitle);
        assertThat(recipes.get(0).getRecipeContent()).isEqualTo(recipeContent);
        assertThat(recipes.get(0).getRecipeServing()).isEqualTo(recipeServing);
        assertThat(recipes.get(0).getCookingTime()).isEqualTo(cookingTime);
        assertThat(recipes.get(0).getIngredientsAmount()).isEqualTo(ingredientsAmount);
        assertThat(recipes.get(0).getCookingStep()).isEqualTo(cookingStep);
        assertThat(recipes.get(0).getRecipeLevel()).isEqualTo(recipeLevel);
    }

    @DisplayName("findAllRecipes: 레시피 목록 조회에 성공한다.")
    @Test
    public void findAllRecipes() throws Exception {
        final String url = "/api/admin/recipes";
        final String recipeTitle = "recipeTitle";
        final String recipeContent = "recipeContent";
        final String recipeServing = "recipeServing";
        final String cookingTime = "cookingTime";
        final String ingredientsAmount = "ingredientsAmount";
        final String cookingStep = "cookingStep";
        final String recipeLevel = "recipeLevel";

        recipeRepository.save(Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .recipeServing(recipeServing)
                .cookingTime(cookingTime)
                .ingredientsAmount(ingredientsAmount)
                .cookingStep(cookingStep)
                .recipeLevel(recipeLevel)
                .build());

        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipeLevel").value(recipeLevel))
                .andExpect(jsonPath("$[0].cookingStep").value(cookingStep))
                .andExpect(jsonPath("$[0].ingredientsAmount").value(ingredientsAmount))
                .andExpect(jsonPath("$[0].cookingTime").value(cookingTime))
                .andExpect(jsonPath("$[0].recipeServing").value(recipeServing))
                .andExpect(jsonPath("$[0].recipeContent").value(recipeContent))
                .andExpect(jsonPath("$[0].recipeTitle").value(recipeTitle));

    }

    @DisplayName("findRecipe: 레시피 상세 조회에 성공한다.")
    @Test
    public void findRecipe() throws Exception {
        final String url = "/api/admin/recipes/{id}";
        final String recipeTitle = "recipeTitle";
        final String recipeContent = "recipeContent";
        final String recipeServing = "recipeServing";
        final String cookingTime = "cookingTime";
        final String ingredientsAmount = "ingredientsAmount";
        final String cookingStep = "cookingStep";
        final String recipeLevel = "recipeLevel";

        Recipe savedRecipe = recipeRepository.save(Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .recipeServing(recipeServing)
                .cookingTime(cookingTime)
                .ingredientsAmount(ingredientsAmount)
                .cookingStep(cookingStep)
                .recipeLevel(recipeLevel)
                .build());

        final ResultActions resultActions = mockMvc.perform(get(url, savedRecipe.getId()));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeLevel").value(recipeLevel))
                .andExpect(jsonPath("$.cookingStep").value(cookingStep))
                .andExpect(jsonPath("$.ingredientsAmount").value(ingredientsAmount))
                .andExpect(jsonPath("$.cookingTime").value(cookingTime))
                .andExpect(jsonPath("$.recipeServing").value(recipeServing))
                .andExpect(jsonPath("$.recipeContent").value(recipeContent))
                .andExpect(jsonPath("$.recipeTitle").value(recipeTitle));
    }

    @DisplayName("deleteRecipe: 레시피 삭제에 성공한다.")
    @Test
    public void deleteRecipe() throws Exception {
        final String url = "/api/admin/recipes/{id}";
        final String recipeTitle = "recipeTitle";
        final String recipeContent = "recipeContent";
        final String recipeServing = "recipeServing";
        final String cookingTime = "cookingTime";
        final String ingredientsAmount = "ingredientsAmount";
        final String cookingStep = "cookingStep";
        final String recipeLevel = "recipeLevel";


        Recipe savedRecipe = recipeRepository.save(Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .recipeServing(recipeServing)
                .cookingTime(cookingTime)
                .ingredientsAmount(ingredientsAmount)
                .cookingStep(cookingStep)
                .recipeLevel(recipeLevel)
                .build());

        mockMvc.perform(delete(url, savedRecipe.getId()))
                .andExpect(status().isOk());

        List<Recipe> recipes = recipeRepository.findAll();

        assertThat(recipes).isEmpty();
    }

    @DisplayName("updateRecipe: 레시피 수정에 성공한다.")
    @Test
    public void updateRecipe() throws Exception {
        final String url = "/api/admin/recipes/{id}";
        final String recipeTitle = "recipeTitle";
        final String recipeContent = "recipeContent";
        final String recipeServing = "recipeServing";
        final String cookingTime = "cookingTime";
        final String ingredientsAmount = "ingredientsAmount";
        final String cookingStep = "cookingStep";
        final String recipeLevel = "recipeLevel";

        Recipe savedRecipe = recipeRepository.save(Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .recipeServing(recipeServing)
                .cookingTime(cookingTime)
                .ingredientsAmount(ingredientsAmount)
                .cookingStep(cookingStep)
                .recipeLevel(recipeLevel)
                .build());

        final String newRecipeTitle = "new recipeTitle";
        final String newRecipeContent = "new recipeContent";
        final String newRecipeServing = "new recipeServing";
        final String newCookingTime = "new cookingTime";
        final String newIngredientsAmount = "new ingredientsAmount";
        final String newCookingStep = "new cookingStep";
        final String newRecipeLevel = "new recipeLevel";

        UpdateRecipeRequest request = new UpdateRecipeRequest(newRecipeTitle, newRecipeContent, newRecipeServing, newCookingTime, newIngredientsAmount, newCookingStep, newRecipeLevel);

        ResultActions result = mockMvc.perform(put(url, savedRecipe.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Recipe recipe = recipeRepository.findById(savedRecipe.getId()).get();

        assertThat(recipe.getRecipeTitle()).isEqualTo(newRecipeTitle);
        assertThat(recipe.getRecipeContent()).isEqualTo(newRecipeContent);
        assertThat(recipe.getRecipeServing()).isEqualTo(newRecipeServing);
        assertThat(recipe.getCookingTime()).isEqualTo(newCookingTime);
        assertThat(recipe.getIngredientsAmount()).isEqualTo(newIngredientsAmount);
        assertThat(recipe.getCookingStep()).isEqualTo(newCookingStep);
        assertThat(recipe.getRecipeLevel()).isEqualTo(newRecipeLevel);
    }


}