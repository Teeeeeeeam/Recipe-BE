package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.team.RecipeRadar.domain.recipe.api.user.OrderType;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.CookStepDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients.*;
import static com.team.RecipeRadar.domain.recipe.domain.type.CookMethods.*;
import static com.team.RecipeRadar.domain.recipe.domain.type.DishTypes.*;
import static org.assertj.core.api.Assertions.*;

@Import(QueryDslConfig.class)
@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired RecipeRepository recipeRepository;
    @Autowired IngredientRepository ingredientRepository;
    @Autowired CookStepRepository cookStepRepository;
    @Autowired ImgRepository imgRepository;

    private List<Recipe> recipes;
    private List<UploadFile> uploadFiles;
    private List<Ingredient> ingredients;
    private List<CookingStep> cookingSteps;

    @BeforeEach
    void setUp(){
        recipes = List.of(
                Recipe.builder().title("제목1").cookingTime("시간1").likeCount(10).cookMethods(BOILING).cookingIngredients(MEAT).build(),
                Recipe.builder().title("제목2").cookingTime("시간2").likeCount(2).cookingIngredients(BEEF).build(),
                Recipe.builder().title("제목3").cookingTime("시간3").likeCount(4).cookingIngredients(BEEF).build(),
                Recipe.builder().title("제목4").cookingTime("시간4").likeCount(6).cookingIngredients(FLOUR).build(),
                Recipe.builder().title("제목5").cookingTime("시간5").likeCount(124).types(BREAD).build()
        );
        recipeRepository.saveAll(recipes);

        uploadFiles = List.of(
                UploadFile.builder().recipe(recipes.get(0)).storeFileName("test").originFileName("test").build(),
                UploadFile.builder().recipe(recipes.get(1)).storeFileName("test").originFileName("test").build(),
                UploadFile.builder().recipe(recipes.get(2)).storeFileName("test").originFileName("test").build(),
                UploadFile.builder().recipe(recipes.get(3)).storeFileName("test").originFileName("test").build(),
                UploadFile.builder().recipe(recipes.get(4)).storeFileName("test").originFileName("test").build()
        );
        imgRepository.saveAll(uploadFiles);

         ingredients = List.of(
                Ingredient.builder().recipe(recipes.get(0)).ingredients("밥").build(),
                Ingredient.builder().recipe(recipes.get(1)).ingredients("밥").build(),
                Ingredient.builder().recipe(recipes.get(2)).ingredients("밥").build(),
                Ingredient.builder().recipe(recipes.get(3)).ingredients("밥").build(),
                Ingredient.builder().recipe(recipes.get(4)).ingredients("밥").build()
        );

         ingredientRepository.saveAll(ingredients);

        cookingSteps = List.of(
                CookingStep.builder().recipe(recipes.get(0)).steps("조리순서").build(),
                CookingStep.builder().recipe(recipes.get(1)).steps("조리순서").build(),
                CookingStep.builder().recipe(recipes.get(2)).steps("조리순서").build(),
                CookingStep.builder().recipe(recipes.get(3)).steps("조리순서").build(),
                CookingStep.builder().recipe(recipes.get(4)).steps("조리순서").build()
        );
        cookStepRepository.saveAll(cookingSteps);
    }

    @Test
    @DisplayName("무한 페이징(Slice) 테스트 ")
    void findIng(){
        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Slice<RecipeDto> recipe_FirstPage = recipeRepository.getRecipe(ingredients, null,Pageable.ofSize(2));

        List<RecipeDto> content = recipe_FirstPage.getContent();
        assertThat(content.get(0).getId()).isEqualTo(recipes.get(0).getId());
        assertThat(recipe_FirstPage.hasNext()).isTrue();

        Slice<RecipeDto> recipe_lastPage = recipeRepository.getRecipe(ingredients, recipes.get(4).getId(),Pageable.ofSize(2));
        assertThat(recipe_lastPage.getContent()).hasSize(0);
        assertThat(recipe_lastPage.hasNext()).isFalse();
    }

    @Test
    @DisplayName("레시피의 상세 조회 테스트")
    void getDetails_recipe(){
        RecipeDto recipeDetails = recipeRepository.getRecipeDetails(recipes.get(0).getId());
        RecipeDto dto = recipeDetails.toDto();
        String ing = recipeDetails.getIngredient();
        List<CookStepDto> cookSteps = recipeDetails.getCookSteps();

        assertThat(dto.getTitle()).isEqualTo(recipes.get(0).getTitle());
        assertThat(ing).isEqualTo(ingredients.get(0).getIngredients());
        assertThat(cookSteps.get(0).getCookSteps()).isEqualTo(cookSteps.get(0).getCookSteps());
    }

    @Test
    @DisplayName("레시피 검색의 일반 페이징 방식 테스트")
    void search_ing_normal_page(){
        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Pageable pageable = PageRequest.of(0, 2);
        Page<RecipeDto> recipeDtoPage = recipeRepository.getNormalPage(List.of(), "제목1",pageable);

        assertThat(recipeDtoPage.getTotalPages()).isEqualTo(1);
        assertThat(recipeDtoPage.getContent().get(0).getTitle()).isEqualTo(recipes.get(0).getTitle());
        assertThat(recipeDtoPage.getTotalElements()).isEqualTo(1);

        Pageable pageable1 = PageRequest.of(3, 2);
        Page<RecipeDto> NorecipeDtoPage = recipeRepository.getNormalPage(ingredients, "",pageable1);
        assertThat(NorecipeDtoPage.getContent()).hasSize(0);
    }


    @Test
    @DisplayName("메인페이지의 레시피 좋아요가 많은순 출력")
    void main_Page_like_desc(){
        List<RecipeDto> recipeDtoList = recipeRepository.mainPageRecipe();

        assertThat(recipeDtoList.get(0).getLikeCount()).isEqualTo(124);
        assertThat(recipeDtoList.get(1).getLikeCount()).isEqualTo(10);
        assertThat(recipeDtoList.get(2).getLikeCount()).isEqualTo(6);
        assertThat(recipeDtoList.get(3).getLikeCount()).isEqualTo(4);
        assertThat(recipeDtoList.get(4).getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("레시피 업데이트 테스트")
    void recipe_update(){
        recipes.get(0).updateRecipe("변경된 제목","변경된 레벨","변경된 인원수","변경된 시간", CHICKEN, BOILING, SIDE_DISH);

        ingredients.get(0).setIngredients("변경한재료!");

        cookingSteps.get(0).setSteps("변경 된 조리순서1");

        assertThat(recipes.get(0).getTitle()).isEqualTo("변경된 제목");
        assertThat(recipes.get(0).getTitle()).isNotEqualTo("레시피1");
        assertThat(ingredients.get(0).getIngredients()).isEqualTo("변경한재료!");
        assertThat(cookingSteps.get(0).getSteps()).isEqualTo("변경 된 조리순서1");
    }

    @Test
    @DisplayName("무한 페이징(Slice) 테스트 _어드민")
    void admin_find_titleAndIng(){
        List<String>  ingredients = new ArrayList<>();
        ingredients.add("밥");

        Pageable pageRequest_nextPageTrue = PageRequest.of(0, 10);

        Slice<RecipeDto> recipe_FirstPage = recipeRepository.adminSearchTitleOrIng(ingredients,recipes.get(2).getTitle() ,recipes.get(1).getId(),pageRequest_nextPageTrue);
        Slice<RecipeDto> recipe_FirstPage_2 = recipeRepository.adminSearchTitleOrIng(ingredients,"제목" ,null,pageRequest_nextPageTrue);

        // 하나의 데이터만 search
        assertThat(recipe_FirstPage.getContent()).hasSize(1);

        assertThat(recipe_FirstPage.hasNext()).isFalse();

        // 모든 레시피 데이터 검색
        assertThat(recipe_FirstPage_2.getContent()).hasSize(5);

    }
    @Test
    @DisplayName("레시피 수 조회")
    void recipeCount(){
        long count = recipeRepository.countAllBy();
        assertThat(count).isEqualTo(5l);
    }

    @Test
    @DisplayName("카테고리 테스트")
    void categoryTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Slice<RecipeDto> recipeDtos = recipeRepository.searchCategory(
                List.of(BEEF), null, null, OrderType.DATE, null, null, pageRequest);

        assertThat(recipeDtos).isNotNull();
        assertThat(recipeDtos.getContent()).isNotEmpty();
        assertThat(recipeDtos.getContent()).hasSize(2);
    }
}