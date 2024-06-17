package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.CookStepDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired RecipeRepository recipeRepository;
    @Autowired IngredientRepository ingredientRepository;
    @Autowired CookStepRepository cookStepRepository;
    @Autowired ImgRepository imgRepository;


    @Test
    @DisplayName("무한 페이징(Slice) 테스트 ")
    void findIng(){

        Recipe build1 = Recipe.builder().id(1l).title("제목1").cookingTime("시간1").build();
        Recipe build2 = Recipe.builder().id(2l).title("제목2").cookingTime("시간2").build();
        Recipe build3 = Recipe.builder().id(3l).title("제목3").cookingTime("시간3").build();
        Recipe build4 = Recipe.builder().id(4l).title("제목4").cookingTime("시간4").build();
        Recipe build5 = Recipe.builder().id(5l).title("제목5").cookingTime("시간5").build();

        Recipe save1 = recipeRepository.save(build1);
        Recipe save2 = recipeRepository.save(build2);
        Recipe save3 = recipeRepository.save(build3);
        Recipe save4 = recipeRepository.save(build4);
        Recipe save5 = recipeRepository.save(build5);
        UploadFile uploadFile1 = UploadFile.builder().recipe(save1).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile2 = UploadFile.builder().recipe(save2).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile3 = UploadFile.builder().recipe(save3).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile4= UploadFile.builder().recipe(save4).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile5 = UploadFile.builder().recipe(save5).storeFileName("test").originFileName("test").build();
        imgRepository.save(uploadFile1);
        imgRepository.save(uploadFile2);
        imgRepository.save(uploadFile3);
        imgRepository.save(uploadFile4);
        imgRepository.save(uploadFile5);

        Ingredient ingredient = Ingredient.builder().recipe(save1).ingredients("밥").build();
        Ingredient ingredient1 = Ingredient.builder().recipe(save2).ingredients("밥|고기").build();
        Ingredient ingredient2 = Ingredient.builder().recipe(save3).ingredients("밥|김치").build();
        Ingredient ingredient3 = Ingredient.builder().recipe(save4).ingredients("밥|돼지고기|밑반찬").build();
        Ingredient ingredient4 = Ingredient.builder().recipe(save5).ingredients("밥|물김치|닭고기").build();

        ingredientRepository.save(ingredient);
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        Ingredient save = ingredientRepository.save(ingredient4);


        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Pageable pageRequest_nextPageTrue = PageRequest.of(0, 2);

        Slice<RecipeDto> recipe_FirstPage = recipeRepository.getRecipe(ingredients, null,pageRequest_nextPageTrue);

        List<RecipeDto> content = recipe_FirstPage.getContent();
        assertThat(content.get(0).getId()).isEqualTo(1l);
        assertThat(recipe_FirstPage.hasNext()).isTrue();


        Pageable pageRequest_nextPageFail = PageRequest.of(2, 2);

        Slice<RecipeDto> recipe_lastPage = recipeRepository.getRecipe(ingredients, save.getRecipe().getId(),pageRequest_nextPageFail);
        assertThat(recipe_lastPage.getContent()).hasSize(0);
        assertThat(recipe_lastPage.hasNext()).isFalse();
    }

    @Test
    @DisplayName("레시피의 상세 조회 테스트")
    void getDetails_recipe(){
        Recipe recipe = Recipe.builder().id(1l).title("레시피 1").people("인원수").build();
        Recipe save = recipeRepository.save(recipe);
        Ingredient ingredient = Ingredient.builder().recipe(save).ingredients("김치|밥|고가").build();
        CookingStep cookingStep = CookingStep.builder().recipe(save).steps("김치를 넣는다").build();

        UploadFile uploadFile1 = UploadFile.builder().recipe(save).storeFileName("test").originFileName("test").build();

        imgRepository.save(uploadFile1);

        Ingredient ingredient1 = ingredientRepository.save(ingredient);
        CookingStep save1 = cookStepRepository.save(cookingStep);

        RecipeDto recipeDetails = recipeRepository.getRecipeDetails(save.getId());
        RecipeDto dto = recipeDetails.toDto();
        String ing = recipeDetails.getIngredient();
        List<CookStepDto> cookSteps = recipeDetails.getCookSteps();

        assertThat(dto.getTitle()).isEqualTo(save.getTitle());
        assertThat(ing).isEqualTo(ingredient1.getIngredients());
        assertThat(cookSteps.get(0).getCookSteps()).isEqualTo(save1.getSteps());
        
    }
    
    @Test
    @DisplayName("레시피 검색의 일반 페이징 방식 테스트")
    void search_ing_normal_page(){
        Recipe build1 = Recipe.builder().id(1l).title("제목1").cookingTime("시간1").build();
        Recipe build2 = Recipe.builder().id(2l).title("제목2").cookingTime("시간2").build();
        Recipe build3 = Recipe.builder().id(3l).title("제목3").cookingTime("시간3").build();
        Recipe build4 = Recipe.builder().id(4l).title("제목4").cookingTime("시간4").build();
        Recipe build5 = Recipe.builder().id(5l).title("제목5").cookingTime("시간5").build();

        Recipe save1 = recipeRepository.save(build1);
        Recipe save2 = recipeRepository.save(build2);
        Recipe save3 = recipeRepository.save(build3);
        Recipe save4 = recipeRepository.save(build4);
        Recipe save5 = recipeRepository.save(build5);

        UploadFile uploadFile1 = UploadFile.builder().recipe(save1).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile2 = UploadFile.builder().recipe(save2).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile3 = UploadFile.builder().recipe(save3).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile4= UploadFile.builder().recipe(save4).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile5 = UploadFile.builder().recipe(save5).storeFileName("test").originFileName("test").build();
        imgRepository.save(uploadFile1);
        imgRepository.save(uploadFile2);
        imgRepository.save(uploadFile3);
        imgRepository.save(uploadFile4);
        imgRepository.save(uploadFile5);

        Ingredient ingredient = Ingredient.builder().recipe(save1).ingredients("밥").build();
        Ingredient ingredient1 = Ingredient.builder().recipe(save2).ingredients("밥|고기").build();
        Ingredient ingredient2 = Ingredient.builder().recipe(save3).ingredients("밥|김치").build();
        Ingredient ingredient3 = Ingredient.builder().recipe(save4).ingredients("밥|돼지고기|밑반찬").build();
        Ingredient ingredient4 = Ingredient.builder().recipe(save5).ingredients("밥|물김치|닭고기").build();

        ingredientRepository.save(ingredient);
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        ingredientRepository.save(ingredient4);


        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Pageable pageable = PageRequest.of(0, 2);

        Page<RecipeDto> recipeDtoPage = recipeRepository.getNormalPage(List.of(), "제목1",pageable);

        assertThat(recipeDtoPage.getTotalPages()).isEqualTo(1);
        assertThat(recipeDtoPage.getContent().get(0).getTitle()).isEqualTo(save1.getTitle());
        assertThat(recipeDtoPage.getTotalElements()).isEqualTo(1);

        Pageable pageable1 = PageRequest.of(3, 2);

        Page<RecipeDto> NorecipeDtoPage = recipeRepository.getNormalPage(ingredients, "",pageable1);
        assertThat(NorecipeDtoPage.getContent()).hasSize(0);
    }
    
    
    @Test
    @DisplayName("메인페이지의 레시피 좋아요가 많은순 출력")
    void main_Page_like_desc(){
        Recipe high = Recipe.builder().id(1l).title("제목1").cookingTime("시간1").likeCount(10).build();
        Recipe low = Recipe.builder().id(2l).title("제목2").cookingTime("시간2").likeCount(2).build();
        Recipe mid = Recipe.builder().id(3l).title("제목3").cookingTime("시간3").likeCount(6).build();

        Recipe save = recipeRepository.save(high);
        Recipe save1 = recipeRepository.save(low);
        Recipe save2 = recipeRepository.save(mid);

        UploadFile uploadFile1 = UploadFile.builder().recipe(save).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile2 = UploadFile.builder().recipe(save1).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile3 = UploadFile.builder().recipe(save2).storeFileName("test").originFileName("test").build();

        imgRepository.save(uploadFile1);
        imgRepository.save(uploadFile2);
        imgRepository.save(uploadFile3);

        List<RecipeDto> recipeDtoList = recipeRepository.mainPageRecipe();

        assertThat(recipeDtoList.get(0).getLikeCount()).isEqualTo(10);
        assertThat(recipeDtoList.get(1).getLikeCount()).isEqualTo(6);
        assertThat(recipeDtoList.get(2).getLikeCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("레시피 업데이트 테스트")
    void recipe_update(){
        Recipe recipe = Recipe.builder().title("레시피1").cookingLevel("어려움").cookingLevel("난이도").people("인원수").build();
        Ingredient ingredient = Ingredient.builder().ingredients("재료1").recipe(recipe).build();
        CookingStep cookingStep1 = CookingStep.builder().steps("조리순서1").recipe(recipe).build();
        CookingStep cookingStep2 = CookingStep.builder().steps("조리순서2").recipe(recipe).build();


        Recipe recipe_save = recipeRepository.save(recipe);
        Ingredient ingredient_save = ingredientRepository.save(ingredient);
        CookingStep cookingStep_save1 = cookStepRepository.save(cookingStep1);
        CookingStep cookingStep_save2 = cookStepRepository.save(cookingStep2);

        Recipe save_Recipe = recipeRepository.findById(recipe_save.getId()).get();
        recipe_save.update_recipe("변경된 제목","변경된 레벨","변경된 인원수","변경된 시간");

        ingredient_save.setIngredients("변경한재료!");

        cookingStep_save1.setSteps("변경 된 조리순서1");
        cookingStep_save2.setSteps("변경 된 조리순서2");

        Recipe recipe_after = recipeRepository.save(save_Recipe);
        Ingredient ingredient_after = ingredientRepository.save(ingredient_save);
        CookingStep cookingStep_after1 = cookStepRepository.save(cookingStep_save1);
        CookingStep cookingStep_after2 = cookStepRepository.save(cookingStep_save2);

        assertThat(recipe_after.getTitle()).isEqualTo("변경된 제목");
        assertThat(recipe_after.getTitle()).isNotEqualTo("레시피1");
        assertThat(ingredient_after.getIngredients()).isEqualTo("변경한재료!");
        assertThat(cookingStep_after1.getSteps()).isEqualTo("변경 된 조리순서1");
        assertThat(cookingStep_after2.getSteps()).isEqualTo("변경 된 조리순서2");
    }

    @Test
    @DisplayName("무한 페이징(Slice) 테스트 _어드민")
    void admin_find_titleAndIng(){

        Recipe build1 = Recipe.builder().id(1l).title("제목1").cookingTime("시간1").build();
        Recipe build2 = Recipe.builder().id(2l).title("제목2").cookingTime("시간2").build();
        Recipe build3 = Recipe.builder().id(3l).title("제목").cookingTime("시간3").build();
        Recipe build4 = Recipe.builder().id(4l).title("제목").cookingTime("시간4").build();
        Recipe build5 = Recipe.builder().id(5l).title("제목5").cookingTime("시간5").build();

        Recipe save1 = recipeRepository.save(build1);
        Recipe save2 = recipeRepository.save(build2);
        Recipe save3 = recipeRepository.save(build3);
        Recipe save4 = recipeRepository.save(build4);
        Recipe save5 = recipeRepository.save(build5);

        UploadFile uploadFile1 = UploadFile.builder().recipe(save1).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile2 = UploadFile.builder().recipe(save2).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile3 = UploadFile.builder().recipe(save3).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile4= UploadFile.builder().recipe(save4).storeFileName("test").originFileName("test").build();
        UploadFile uploadFile5 = UploadFile.builder().recipe(save5).storeFileName("test").originFileName("test").build();
        imgRepository.save(uploadFile1);
        imgRepository.save(uploadFile2);
        imgRepository.save(uploadFile3);
        imgRepository.save(uploadFile4);
        imgRepository.save(uploadFile5);

        Ingredient ingredient = Ingredient.builder().recipe(save1).ingredients("밥").build();
        Ingredient ingredient1 = Ingredient.builder().recipe(save2).ingredients("밥|고기").build();
        Ingredient ingredient2 = Ingredient.builder().recipe(save3).ingredients("밥|김치").build();
        Ingredient ingredient3 = Ingredient.builder().recipe(save4).ingredients("밥|돼지고기|밑반찬").build();
        Ingredient ingredient4 = Ingredient.builder().recipe(save5).ingredients("밥|물김치|닭고기").build();

        ingredientRepository.save(ingredient);
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        Ingredient save = ingredientRepository.save(ingredient4);


        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Pageable pageRequest_nextPageTrue = PageRequest.of(0, 10);

        Slice<RecipeDto> recipe_FirstPage = recipeRepository.adminSearchTitleOrIng(ingredients,save5.getTitle() ,save4.getId(),pageRequest_nextPageTrue);
        Slice<RecipeDto> recipe_FirstPage_2 = recipeRepository.adminSearchTitleOrIng(ingredients,"제목" ,null,pageRequest_nextPageTrue);

        // 하나의 데이터만 search
        assertThat(recipe_FirstPage.getContent()).hasSize(1);

        assertThat(recipe_FirstPage.hasNext()).isFalse();

        // 모든 레시피 데이터 검색
        assertThat(recipe_FirstPage_2.getContent()).hasSize(5);

    }
}