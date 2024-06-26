package com.team.RecipeRadar.domain.recipe.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.recipe.application.user.RecipeServiceImpl;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.domain.Image.application.ImgServiceImpl;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.recipe.dto.response.MainPageRecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeDetailsResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeNormalPageResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeResponse;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean RecipeServiceImpl recipeService;
    @MockBean ImgServiceImpl imgService;
    @MockBean S3UploadService s3UploadService;
    private ObjectMapper objectMapper = new ObjectMapper();



    @Test
    @DisplayName("재료 검색 레시피 조회 테스트")
    void Search_Recipe() throws Exception {

        List<String> ingredients = Arrays.asList("밥");
        Pageable pageRequest = PageRequest.of(0, 2);

        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1L, "url1", "레시피1", "level1", "1인분", "10분", 0, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(2L, "url2", "레시피2", "level2", "2인분", "1시간", 0, LocalDateTime.now()));

        boolean paged = pageRequest.next().isPaged();

        RecipeResponse recipeResponse = new RecipeResponse(recipeDtoList, paged);

        given(recipeService.searchRecipesByIngredients(eq(ingredients), eq(1l),any(Pageable.class)))
                .willReturn(recipeResponse);

        mockMvc.perform(get("/api/recipe?ingredients=밥&lastId=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipeDtoList.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].imageUrl").value("url1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].title").value("레시피1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].cookingLevel").value("level1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].people").value("1인분"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].cookingTime").value("10분"))
                .andExpect(jsonPath("$.data.recipeDtoList.size()").value(2));
    }

    @Test
    @DisplayName("레시피 상세 페이지 조회 테스트")
    void getDetails_Recipe() throws Exception {

        Long id = 1l;
        RecipeDto recipeDto = RecipeDto.builder().id(id).cookingLevel("11").title("title").build();
        List<String> ing= Arrays.asList("밥","고기","김치");
        List<Map<String,String>> cookSetp = new ArrayList<>();

        List<CookingStep> cookingStepList = List.of(CookingStep.builder().id(1l).steps("조리1").build(), CookingStep.builder().id(2l).steps("조리2").build(),
                CookingStep.builder().id(3l).steps("조리3").build());

        for (CookingStep cookingStep : cookingStepList) {
            Map<String,String> map = new LinkedHashMap<>();
            map.put("cookStepId", String.valueOf(cookingStep.getId()));
            map.put("cookSteps", cookingStep.getSteps());
            cookSetp.add(map);
        }

        RecipeDetailsResponse recipeDetailsResponse = RecipeDetailsResponse.of(recipeDto, ing, cookSetp);
        given(recipeService.getRecipeDetails(eq(1l))).willReturn(recipeDetailsResponse);

        mockMvc.perform(get("/api/recipe/"+id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipe.id").value("1"))
                .andExpect(jsonPath("$.data.recipe.title").value("title"))
                .andExpect(jsonPath("$.data.ingredients.size()").value(3))
                .andExpect(jsonPath("$.data.cookSteps.size()").value(3));
    }

    @Test
    @DisplayName("재료 검색 레시피 조회 일반 페이지 네이션 테스트_제목으로만 검색")
    void Search_Recipe_Normal_Page() throws Exception {

        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1L, "url1", "레시피1", "level1", "1인분", "10분", 0, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(2L, "url2", "레시피1", "level2", "2인분", "1시간", 0, LocalDateTime.now()));


        List<RecipeDto> dummyRecipes = Arrays.asList(
                RecipeDto.builder().id(1l).title("제목").build(),
                RecipeDto.builder().id(2l).title("제목").build(),
                RecipeDto.builder().id(3l).title("제목").build(),
                RecipeDto.builder().id(4l).title("제목").build(),
                RecipeDto.builder().id(5l).title("제목").build()
        );

        RecipeNormalPageResponse dummyResponse = new RecipeNormalPageResponse(dummyRecipes, 1, dummyRecipes.size());


        given(recipeService.searchRecipeByIngredientsNormal(isNull(),eq("레시피1"),isNull(),any(Pageable.class)))
                .willReturn(dummyResponse);

        mockMvc.perform(get("/api/recipe/normal?title=레시피1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipes.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipes.size()").value(5));
    }
    @Test
    @DisplayName("재료 검색 레시피 조회 일반 페이지 네이션 테스트_재료으로만 검색")
    void Search_Recipe_Normal_Page_ing() throws Exception {

        List<String> ingredients = Arrays.asList("밥");

        List<RecipeDto> dummyRecipes = Arrays.asList(
                RecipeDto.builder().id(1l).title("제목1").build(),
                RecipeDto.builder().id(2l).title("제목2").build(),
                RecipeDto.builder().id(3l).title("제목3").build(),
                RecipeDto.builder().id(4l).title("제목4").build(),
                RecipeDto.builder().id(5l).title("제목5").build()
        );

        RecipeNormalPageResponse dummyResponse = new RecipeNormalPageResponse(dummyRecipes, 1, dummyRecipes.size());


        given(recipeService.searchRecipeByIngredientsNormal(eq(ingredients),isNull(),isNull(),any(Pageable.class))).willReturn(dummyResponse);

        mockMvc.perform(get("/api/recipe/normal?ingredients=밥")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipes.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipes.size()").value(5));
    }

    @Test
    @DisplayName("메인 페이지에서 레시피 좋아요순으로 출력")
    void main_Page_Recipe_like_desc() throws Exception {
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 16, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 13, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(3l, "url", "레시피3", "level2", "3", "1hour", 3, LocalDateTime.now()));
        MainPageRecipeResponse of = MainPageRecipeResponse.of(recipeDtoList);

        given(recipeService.mainPageRecipe()).willReturn(of);

        mockMvc.perform(get("/api/main/recipe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipe.size()").value(3))
                .andExpect(jsonPath("$.data.recipe.[0].likeCount").value(16))
                .andExpect(jsonPath("$.data.recipe.[1].likeCount").value(13))
                .andExpect(jsonPath("$.data.recipe.[2].likeCount").value(3));
    }

}