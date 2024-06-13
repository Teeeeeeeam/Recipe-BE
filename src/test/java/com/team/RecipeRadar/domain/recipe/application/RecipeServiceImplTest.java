package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.CookStepRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {


    @Mock RecipeRepository recipeRepository;
    @InjectMocks RecipeServiceImpl recipeService;

    @Test
    @DisplayName("무한 페이징 쿼리 테스트")
    void get_Search_Recipe(){

        List<String> ingLists = Arrays.asList("밥");
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 0));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 0));

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

        List<CookingStep> cookingSteps = new ArrayList<>();

        CookingStep cookingStep = CookingStep.builder().steps("순서 1").build();
        CookingStep cookingStep1 = CookingStep.builder().steps("순서 2").build();

        cookingSteps.add(cookingStep);
        cookingSteps.add(cookingStep1);

        RecipeDto fakeRecipeDto = new RecipeDto();
        fakeRecipeDto.setId(1L);
        fakeRecipeDto.setTitle("title");
        fakeRecipeDto.setIngredient("재료1|재료2");
        fakeRecipeDto.setCookingSteps(cookingSteps);

        when(recipeRepository.getRecipeDetails(1L)).thenReturn(fakeRecipeDto);

        RecipeDetailsResponse response = recipeService.getRecipeDetails(1L);

        assertThat(response.getRecipe().getTitle()).isEqualTo(fakeRecipeDto.getTitle());
        assertThat(response.getIngredients().get(0)).isEqualTo("재료1");
        assertThat(response.getCookStep().get(0).get("cook_steps")).isEqualTo("순서 1");
        assertThat(response.getCookStep().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("일반 페이지네이션 테스트")
    void get_Search_RecipeFor_NormalPage(){

        List<String> ingLists = Arrays.asList("밥");
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 0));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 0));

        Pageable pageRequest = PageRequest.of(0, 2);

        PageImpl<RecipeDto> dtoPage = new PageImpl<>(recipeDtoList, pageRequest, 2);

        when(recipeRepository.getNormalPage(eq(ingLists),anyString(),eq(pageRequest))).thenReturn(dtoPage);

        Page<RecipeDto> recipeDtos = recipeService.searchRecipeByIngredientsNormal(ingLists, "title" ,pageRequest);
        assertThat(recipeDtos.getTotalPages()).isEqualTo(1);
        assertThat(recipeDtos.getContent().get(0).getTitle()).isEqualTo("레시피1");
        assertThat(recipeDtos.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("메인 페이지의 좋아요순 service 테스트")
    void main_page_like_desc_conversion(){
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 16));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 13));
        recipeDtoList.add(new RecipeDto(3l, "url", "레시피3", "level2", "3", "1hour", 3));

        when(recipeRepository.mainPageRecipe()).thenReturn(recipeDtoList);

        MainPageRecipeResponse mainPageRecipeResponse = recipeService.mainPageRecipe();

        assertThat(mainPageRecipeResponse.getRecipe()).hasSize(3);
        assertThat(mainPageRecipeResponse.getRecipe().get(0).getLikeCount()).isEqualTo(16);
        assertThat(mainPageRecipeResponse.getRecipe().get(1).getLikeCount()).isEqualTo(13);
        assertThat(mainPageRecipeResponse.getRecipe().get(2).getLikeCount()).isEqualTo(3);
    }

}