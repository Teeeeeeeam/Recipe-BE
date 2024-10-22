package com.team.RecipeRadar.domain.recipe.application.admin;

import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.CookStepRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminRecipeServiceTest {

    @Mock RecipeRepository recipeRepository;
    @Mock IngredientRepository ingredientRepository;
    @Mock CookStepRepository cookStepRepository;
    @Mock S3UploadService s3UploadService;
    @Mock ImgRepository imgRepository;

    @InjectMocks
    AdminRecipeServiceImpl adminService;


    @Test
    @DisplayName("전체 요리글수 조회")
    void count_Recipes(){
        long count =1123123123;

        when(recipeRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllRecipes();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("레시피 저장 테스트")
    void saveRecipe(){
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps, CookIngredients.BEEF, CookMethods.SASHIMI, DishTypes.BREAD);
        Recipe entity = Recipe.createRecipe(recipeSaveRequest.getTitle(),recipeSaveRequest.getCookTime(),recipeSaveRequest.getCookLevel(),recipeSaveRequest.getPeople(),recipeSaveRequest.getCookIngredients(),recipeSaveRequest.getCookMethods(),recipeSaveRequest.getDishTypes());
        Ingredient ingredient = Ingredient.builder().id(1L).ingredients("재료").recipe(entity).build();
        List<CookingStep> cookingSteps = cooksteps.stream()
                .map(s -> CookingStep.builder().steps(s).recipe(entity).build())
                .collect(Collectors.toList());

        when(recipeRepository.save(any(Recipe.class))).thenReturn(entity);
        when(cookStepRepository.saveAll(anyList())).thenReturn(cookingSteps);

        MockMultipartFile file = new MockMultipartFile("test", "Test", "image/jpeg", "test".getBytes());
        adminService.saveRecipe(recipeSaveRequest,file);


        assertThat(entity.getTitle()).isEqualTo(recipeSaveRequest.getTitle());
        assertThat(ingredient.getRecipe()).isEqualTo(entity);
    }

    @Test
    @DisplayName("레시피 수정 성공하는 테스트")
    void updateRecipe_successful() {
        Long recipeId = 1L;
        String title = "변경된 Recipe";
        List<Map<String, String>> cookSteps = new ArrayList<>();
        Map<String, String> cookStep1 = new HashMap<>();
        cookStep1.put("cookStepId", "1");
        cookStep1.put("cookSteps", "조리 순서 1");
        cookSteps.add(cookStep1);
        List<String> ingredients = Arrays.asList("재료 1", "재료 2");
        String originalFileName = "after.jpg";
        MockMultipartFile file = new MockMultipartFile("file", originalFileName, "image/jpeg", "test data".getBytes());

        Recipe testRecipe = Recipe.builder().id(recipeId).title("변경전 타이틀").build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));

        CookingStep testCookStep = new CookingStep();
        when(cookStepRepository.findById(anyLong())).thenReturn(Optional.of(testCookStep));

        UploadFile testUploadFile = new UploadFile("before.jpg", "저장돤 파일명");

        List<Long> delete = List.of(1L);
        adminService.updateRecipe(recipeId, new RecipeUpdateRequest(title, "레벨", "인원수", ingredients, "시간", cookSteps, List.of("새로운 데이터"), delete,CookIngredients.BEEF,CookMethods.SASHIMI,DishTypes.BREAD), file);

        assertThat(testRecipe.getTitle()).isEqualTo(title);
        assertThat(testCookStep.getSteps()).isEqualTo("조리 순서 1");
        assertThat(testUploadFile.getOriginFileName()).isNotEqualTo(originalFileName); // 변경 전과 파일명이 같은지 확인
        verify(ingredientRepository, times(1)).updateRecipeIngredient(eq(recipeId), anyString()); // 인자 매처를 사용하도록 수정
    }


    @Test
    @DisplayName("레피시 변경시 해당 레피시가 존재하지 않을때")
    void notEmpty_recipe(){
        long recipe_id = 1l;
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data ".getBytes());
        when(recipeRepository.findById(eq(recipe_id))).thenThrow(new NoSuchElementException("해당 레시피를 찾을수 업습니다."));

        assertThatThrownBy(()-> adminService.updateRecipe(recipe_id,recipeUpdateRequest,multipartFile)).isInstanceOf(NoSuchElementException.class);
    }
}