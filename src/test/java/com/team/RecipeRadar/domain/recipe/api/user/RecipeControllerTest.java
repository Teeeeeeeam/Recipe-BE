package com.team.RecipeRadar.domain.recipe.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.recipe.application.user.RecipeServiceImpl;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.domain.Image.application.ImgServiceImpl;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.recipe.dto.response.*;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDate;
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

    private List<RecipeDto> recipeDtos;
    private  List<String> ingredients;
    @BeforeEach
    void setUp(){
        ingredients = Arrays.asList("밥");
        recipeDtos = List.of(
                RecipeDto.builder().id(1l).title("레시피1").likeCount(16).cookIngredients(CookIngredients.BEEF).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(2l).title("레시피2").likeCount(13).cookIngredients(CookIngredients.FLOUR).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(3l).title("레시피3").likeCount(3).dishTypes(DishTypes.MAIN_DISH).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(4l).title("레시피4").likeCount(2).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(5l).title("레시피5").likeCount(1).createdAt(LocalDate.now()).build()
        );
    }

    @Test
    @DisplayName("재료 검색 레시피 조회 테스트")
    void Search_Recipe() throws Exception {

        Pageable pageRequest = PageRequest.of(0, 2);

        boolean paged = pageRequest.next().isPaged();

        RecipeResponse recipeResponse = new RecipeResponse(recipeDtos, paged);

        given(recipeService.searchRecipesByIngredients(eq(ingredients), eq(1l),any(Pageable.class)))
                .willReturn(recipeResponse);

        mockMvc.perform(get("/api/recipe?ingredients=밥&lastId=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipeDtoList.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].title").value("레시피1"))
                .andExpect(jsonPath("$.data.recipeDtoList.size()").value(5));
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
    @DisplayName("재료 검색시 카테고리+재료+시간순 정렬 테스트")
    void Search_Recipe_category_ingredients_date_Page() throws Exception {
        RecipeNormalPageResponse dummyResponse = new RecipeNormalPageResponse(false, recipeDtos);

        given(recipeService.searchRecipeByIngredientsNormal(eq(ingredients),eq(List.of(CookIngredients.BEEF,CookIngredients.FLOUR)),isNull(),eq(List.of(DishTypes.MAIN_DISH)),isNull(),
                        eq(OrderType.DATE),any(),isNull(),any(Pageable.class)))
                .willReturn(dummyResponse);

        mockMvc.perform(get("/api/recipe/search?ingredients=밥&cat1=BEEF,FLOUR&cat3=MAIN_DISH&order=DATE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.recipes.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipes.size()").value(5));
    }
    @Test
    @DisplayName("레시피 검색시 제목으로 만검색")
    void Search_Recipe_title_Page() throws Exception {

        RecipeNormalPageResponse dummyResponse = new RecipeNormalPageResponse(false,recipeDtos);

        given(recipeService.searchRecipeByIngredientsNormal(isNull(),isNull(),isNull(),isNull(),eq("레시피"), eq(OrderType.DATE),isNull(),isNull(),any(Pageable.class)))
                .willReturn(dummyResponse);

        mockMvc.perform(get("/api/recipe/search?title=레시피")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipes.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipes.size()").value(5));
    }

    @Test
    @DisplayName("메인 페이지에서 레시피 좋아요순으로 출력")
    void main_Page_Recipe_like_desc() throws Exception {
        MainPageRecipeResponse of = MainPageRecipeResponse.of(recipeDtos);

        given(recipeService.mainPageRecipe()).willReturn(of);

        mockMvc.perform(get("/api/recipe/main")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipes.size()").value(5))
                .andExpect(jsonPath("$.data.recipes.[0].likeCount").value(16))
                .andExpect(jsonPath("$.data.recipes.[1].likeCount").value(13))
                .andExpect(jsonPath("$.data.recipes.[2].likeCount").value(3));
    }

}