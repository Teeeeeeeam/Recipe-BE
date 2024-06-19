package com.team.RecipeRadar.domain.recipe.application.user;

import com.team.RecipeRadar.domain.recipe.application.user.RecipeServiceImpl;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.domain.recipe.dto.response.MainPageRecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeDetailsResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeNormalPageResponse;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
class RecipeServiceImplTest {


    @Mock RecipeRepository recipeRepository;
    @InjectMocks
    RecipeServiceImpl recipeService;

    @Test
    @DisplayName("무한 페이징 쿼리 테스트")
    void get_Search_Recipe(){

        List<String> ingLists = Arrays.asList("밥");
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 0, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 0, LocalDateTime.now()));

        Pageable pageRequest = PageRequest.of(0, 2);

        SliceImpl<RecipeDto> recipeDtoSlice = new SliceImpl<>(recipeDtoList);

        when(recipeRepository.getRecipe(eq(ingLists),eq(1l),eq(pageRequest))).thenReturn(recipeDtoSlice);

        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingLists, 1l, pageRequest);

        assertThat(recipeResponse.getNextPage()).isFalse();
        assertThat(recipeResponse.getRecipeDtoList().size()).isEqualTo(2);
        assertThat(recipeResponse.getRecipeDtoList().get(0).getTitle()).isEqualTo("레시피1");
    }

    @Test
    @DisplayName("레시피 상세 페이지")
    void get_Details_Recipe(){

        List<CookStepDto> cookingSteps = new ArrayList<>();

        CookStepDto cookStepDto = new CookStepDto(1l,"순서1");
        CookStepDto cookStepDto1 = new CookStepDto(2l,"순서2");
        cookingSteps.add(cookStepDto);
        cookingSteps.add(cookStepDto1);


        RecipeDto fakeRecipeDto = new RecipeDto();
        fakeRecipeDto.setId(1L);
        fakeRecipeDto.setTitle("title");
        fakeRecipeDto.setIngredient("재료1|재료2");
        fakeRecipeDto.setCookSteps(cookingSteps);
        fakeRecipeDto.setCreatedAt(LocalDate.now());

        when(recipeRepository.getRecipeDetails(1L)).thenReturn(fakeRecipeDto);

        RecipeDetailsResponse response = recipeService.getRecipeDetails(1L);

        assertThat(response.getRecipe().getTitle()).isEqualTo(fakeRecipeDto.getTitle());
        assertThat(response.getIngredients().get(0)).isEqualTo("재료1");
        assertThat(response.getCookSteps().get(0).get("cookSteps")).isEqualTo("순서1");
        assertThat(response.getCookSteps().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("일반 페이지네이션 테스트")
    void get_Search_RecipeFor_NormalPage(){

        List<String> ingLists = Arrays.asList("밥");
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 0, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 0, LocalDateTime.now()));

        Pageable pageRequest = PageRequest.of(0, 2);

        PageImpl<RecipeDto> dtoPage = new PageImpl<>(recipeDtoList, pageRequest, 2);

        when(recipeRepository.getNormalPage(eq(ingLists),anyString(),eq(pageRequest))).thenReturn(dtoPage);

        List<RecipeDto> dummyRecipes = Arrays.asList(
                RecipeDto.builder().id(1l).title("제목").build(),
                RecipeDto.builder().id(2l).title("제목").build(),
                RecipeDto.builder().id(3l).title("제목").build(),
                RecipeDto.builder().id(4l).title("제목").build(),
                RecipeDto.builder().id(5l).title("제목").build()
        );

        RecipeNormalPageResponse dummyResponse = new RecipeNormalPageResponse(dummyRecipes, 1, dummyRecipes.size());


        RecipeNormalPageResponse recipeNormalPageResponse = recipeService.searchRecipeByIngredientsNormal(ingLists, "title", pageRequest);
       log.info("asdasd={}",recipeNormalPageResponse);
    }

    @Test
    @DisplayName("메인 페이지의 좋아요순 service 테스트")
    void main_page_like_desc_conversion(){
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 16, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 13, LocalDateTime.now()));
        recipeDtoList.add(new RecipeDto(3l, "url", "레시피3", "level2", "3", "1hour", 3, LocalDateTime.now()));

        when(recipeRepository.mainPageRecipe()).thenReturn(recipeDtoList);

        MainPageRecipeResponse mainPageRecipeResponse = recipeService.mainPageRecipe();

        assertThat(mainPageRecipeResponse.getRecipe()).hasSize(3);
        assertThat(mainPageRecipeResponse.getRecipe().get(0).getLikeCount()).isEqualTo(16);
        assertThat(mainPageRecipeResponse.getRecipe().get(1).getLikeCount()).isEqualTo(13);
        assertThat(mainPageRecipeResponse.getRecipe().get(2).getLikeCount()).isEqualTo(3);
    }

}