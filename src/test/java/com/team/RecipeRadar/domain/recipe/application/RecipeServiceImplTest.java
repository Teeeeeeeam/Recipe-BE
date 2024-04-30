package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        when(recipeRepository.getRecipe(eq(ingLists),eq(pageRequest))).thenReturn(recipeDtoSlice);

        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingLists, pageRequest);

        assertThat(recipeResponse.getNextPage()).isFalse();
        assertThat(recipeResponse.getRecipeDtoList().size()).isEqualTo(2);
        assertThat(recipeResponse.getRecipeDtoList().get(0).getTitle()).isEqualTo("레시피1");
    }


}