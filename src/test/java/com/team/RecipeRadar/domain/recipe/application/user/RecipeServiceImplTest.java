package com.team.RecipeRadar.domain.recipe.application.user;

import com.team.RecipeRadar.domain.recipe.api.user.OrderType;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.domain.recipe.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
class RecipeServiceImplTest {

    @Mock RecipeRepository recipeRepository;
    @InjectMocks RecipeServiceImpl recipeService;

    private List<RecipeDto> recipeDtoList;
    private List<String> ingLists;
    private List<CookStepDto> cookingSteps;
    @BeforeEach
    void setUp(){
        ingLists = List.of("밥","밥|고기|김치");

        cookingSteps = List.of(
                 new CookStepDto(1l,"순서1"),
                 new CookStepDto(2l,"순서2")
        );
        recipeDtoList = List.of(
                RecipeDto.builder().id(1l).title("레시피1").likeCount(123).ingredient(ingLists.get(0)).cookSteps(cookingSteps).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(2l).title("레시피2").likeCount(22).ingredient(ingLists.get(1)).cookSteps(cookingSteps).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(3l).title("레시피3").likeCount(10).ingredient(ingLists.get(0)).cookSteps(cookingSteps).createdAt(LocalDate.now()).build(),
                RecipeDto.builder().id(4l).title("레시피4").likeCount(4).ingredient(ingLists.get(0)).cookSteps(cookingSteps).createdAt(LocalDate.now()).build()
        );
    }
    @Test
    @DisplayName("무한 페이징 쿼리 테스트")
    void get_Search_Recipe(){
        SliceImpl<RecipeDto> recipeDtoSlice = new SliceImpl<>(recipeDtoList);

        when(recipeRepository.getRecipe(eq(ingLists),eq(1l),any(Pageable.class))).thenReturn(recipeDtoSlice);

        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingLists, 1l, Pageable.ofSize(2));

        assertThat(recipeResponse.getNextPage()).isFalse();
        assertThat(recipeResponse.getRecipeDtoList().size()).isEqualTo(4);
        assertThat(recipeResponse.getRecipeDtoList().get(0).getTitle()).isEqualTo("레시피1");
    }

    @Test
    @DisplayName("레시피 상세 페이지")
    void get_Details_Recipe(){
        when(recipeRepository.getRecipeDetails(anyLong())).thenReturn(recipeDtoList.get(0));

        RecipeDetailsResponse response = recipeService.getRecipeDetails(1l);

        assertThat(response.getRecipe().getTitle()).isEqualTo(recipeDtoList.get(0).getTitle());
        assertThat(response.getIngredients().get(0)).isEqualTo("밥");
        assertThat(response.getCookSteps().get(0).get("cookSteps")).isEqualTo("순서1");
        assertThat(response.getCookSteps().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("검색 무한 스크롤 테스트")
    void get_Search_RecipeFor_NormalPage(){

        Pageable pageRequest = PageRequest.of(0, 2);

        PageImpl<RecipeDto> dtoPage = new PageImpl<>(recipeDtoList, pageRequest, 2);

        when(recipeRepository.userSearchRecipe(anyList(),isNull(),isNull(),isNull(),eq("title"),eq(OrderType.DATE),isNull(),isNull(),any(Pageable.class))).thenReturn(dtoPage);

        RecipeSearchResponse title = recipeService.searchRecipeByIngredientsNormal(ingLists,null,null,null,"title", OrderType.DATE,null,null,Pageable.ofSize(2));
        assertThat(title.getRecipes()).hasSize(4);
    }

    @Test
    @DisplayName("메인 페이지의 좋아요순 service 테스트")
    void main_page_like_desc_conversion(){
        when(recipeRepository.mainPageRecipe()).thenReturn(recipeDtoList);

        MainPageRecipeResponse mainPageRecipeResponse = recipeService.mainPageRecipe();
        assertThat(mainPageRecipeResponse.getRecipes()).hasSize(4);
        assertThat(mainPageRecipeResponse.getRecipes().get(0).getLikeCount()).isEqualTo(123);
        assertThat(mainPageRecipeResponse.getRecipes().get(1).getLikeCount()).isEqualTo(22);
        assertThat(mainPageRecipeResponse.getRecipes().get(2).getLikeCount()).isEqualTo(10);
        assertThat(mainPageRecipeResponse.getRecipes().get(3).getLikeCount()).isEqualTo(4);
    }
}