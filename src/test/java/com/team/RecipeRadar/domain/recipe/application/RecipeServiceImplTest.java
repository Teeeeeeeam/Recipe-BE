package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.CookStepRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {


    @Mock RecipeRepository recipeRepository;
    @Mock IngredientRepository ingredientRepository;
    @Mock CookStepRepository cookStepRepository;
    @Mock ImgRepository imgRepository;
    @Mock S3UploadService s3UploadService;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;

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

    @Test
    @DisplayName("레시피 저장 테스트")
    void saveRecipe(){
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps);
        Recipe entity = Recipe.toEntity(recipeSaveRequest);
        Ingredient ingredient = Ingredient.builder().id(1L).ingredients("재료").recipe(entity).build();
        List<CookingStep> cookingSteps = cooksteps.stream()
                .map(s -> CookingStep.builder().steps(s).recipe(entity).build())
                .collect(Collectors.toList());

        when(recipeRepository.save(any(Recipe.class))).thenReturn(entity);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
        when(cookStepRepository.saveAll(anyList())).thenReturn(cookingSteps);

        recipeService.saveRecipe(recipeSaveRequest,"testURL","IMG");


        assertThat(entity.getTitle()).isEqualTo(recipeSaveRequest.getTitle());
        assertThat(ingredient.getRecipe()).isEqualTo(entity);
    }

    @Test
    @DisplayName("레시피 수정 성공하는 테스트")
    void updateRecipe_successful() throws Exception {
        // Given
        long recipeId = 1L;
        String title = "변경된 Recipe";
        List<Map<String, String>> cookSteps = new ArrayList<>();
        Map<String, String> cookStep1 = new HashMap<>();
        cookStep1.put("cook_step_id", "1");
        cookStep1.put("cook_steps", "조리 순서 1");
        cookSteps.add(cookStep1);
        List<String> ingredients = Arrays.asList("재료 1", "재료 2");
        String originalFileName = "after.jpg";
        MockMultipartFile file = new MockMultipartFile("file", originalFileName, "image/jpeg", "test data".getBytes());

        Recipe testRecipe = Recipe.builder().id(recipeId).title("변경전 타이틀").build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
        
        CookingStep testCookStep = new CookingStep();
        when(cookStepRepository.findById(anyLong())).thenReturn(Optional.of(testCookStep));
        
        UploadFile testUploadFile = new UploadFile("before.jpg","저장돤 파일명");
        doNothing().when(s3UploadService).deleteFile(anyString());
        when(s3UploadService.uploadFile(file)).thenReturn(originalFileName);

        when(imgRepository.findByRecipe_Id(recipeId)).thenReturn(Optional.of(testUploadFile));

        doNothing().when(ingredientRepository).updateRecipe_ing(recipeId, "재료 1|재료 2");

        recipeService.updateRecipe(recipeId, new RecipeUpdateRequest(title, "레벨", "인원수", ingredients, "시간", cookSteps), file);
        
        assertThat(testRecipe.getTitle()).isEqualTo(title);
        assertThat(testCookStep.getSteps()).isEqualTo("조리 순서 1");
        assertThat(testUploadFile.getOriginFileName()).isNotEqualTo("before.jpg");
        assertThat(testUploadFile.getOriginFileName()).isEqualTo("after.jpg");
        verify(ingredientRepository, times(1)).updateRecipe_ing(anyLong(),anyString());
    }

    @Test
    @DisplayName("레피시 변경시 해당 레피시가 존재하지 않을때")
    void notEmpty_recipe(){
        long recipe_id = 1l;
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data ".getBytes());
        when(recipeRepository.findById(eq(recipe_id))).thenThrow(new NoSuchElementException("해당 레시피를 찾을수 업습니다."));

        assertThatThrownBy(()-> recipeService.updateRecipe(recipe_id,recipeUpdateRequest,multipartFile)).isInstanceOf(NoSuchElementException.class);
    }
    @Test
    @DisplayName("어드민 페이지-무한 페이징 쿼리 테스트_제목")
    void get_Search_Admin_Recipe(){

        List<String> ingLists = Arrays.asList("밥");


        String title = "레시피1";
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", title, "level1", "1", "10minute", 0,List.of(),"밥"));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 0));

        Pageable pageRequest = PageRequest.of(0, 2);

        SliceImpl<RecipeDto> recipeDtoSlice = new SliceImpl<>(recipeDtoList);

        when(recipeRepository.adminSearchTitleOrIng(eq(ingLists),eq(title),eq(1l),eq(pageRequest))).thenReturn(recipeDtoSlice);

        RecipeResponse recipeResponse = recipeService.searchRecipesByTitleAndIngredients(ingLists, "레시피1", 1l, pageRequest);

        assertThat(recipeResponse.getNextPage()).isFalse();
        assertThat(recipeResponse.getRecipeDtoList().size()).isEqualTo(2);
        assertThat(recipeResponse.getRecipeDtoList().get(0).getTitle()).isEqualTo("레시피1");
    }

    @Test
    @DisplayName("어드민 페이지-무한 페이징 쿼리 테스트_찾는 값 없을때")
    void get_Search_Admin_Recipe_titleAndIng(){

        List<String> no_ingLists = Arrays.asList("밥");

        List<RecipeDto> recipeDtoList = new ArrayList<>();

        Pageable pageRequest = PageRequest.of(0, 2);

        SliceImpl<RecipeDto> recipeDtoSlice = new SliceImpl<>(recipeDtoList);

        when(recipeRepository.adminSearchTitleOrIng(anyList(),anyString(),anyLong(),eq(pageRequest))).thenReturn(recipeDtoSlice);

        RecipeResponse recipeResponse = recipeService.searchRecipesByTitleAndIngredients(no_ingLists, "a", 1l, pageRequest);

        assertThat(recipeResponse.getNextPage()).isFalse();
        assertThat(recipeDtoList).isEmpty();
    }
    
    @Test
    @DisplayName("관리지만 레시피 삭제가능")
    void deleteAdmin(){
        String loginId= "TestId";
        Long recipeId =1l;
        Member member = Member.builder().id(1l).loginId(loginId).roles("ROLE_ADMIN").build();
        when(memberRepository.findByLoginId(eq(loginId))).thenReturn(member);

        UploadFile uploadFile = new UploadFile();
        uploadFile.setStoreFileName("testfile.jpg");

        when(imgRepository.findByRecipe_Id(anyLong())).thenReturn(Optional.of(uploadFile));

        recipeService.deleteByAdmin(1l, loginId);

        verify(postRepository, times(1)).deleteAllByRecipe_Id(recipeId);
        verify(s3UploadService, times(1)).deleteFile("testfile.jpg");
        verify(ingredientRepository, times(1)).deleteRecipeId(recipeId);
        verify(imgRepository, times(1)).deleteRecipeId(recipeId);
        verify(cookStepRepository, times(1)).deleteRecipeId(recipeId);
        verify(recipeRepository, times(1)).deleteById(recipeId);

    }

    @Test
    @DisplayName("레시피 삭젯 관리자가 아닐때 예외")
    void delete_No_Admin(){
        String loginId= "TestId";
        Long recipeId =1l;
        when(memberRepository.findByLoginId(eq(loginId))).thenThrow(new AccessDeniedException("관리자만 삭제 가능"));

        assertThatThrownBy(() -> recipeService.deleteByAdmin(recipeId,loginId)).isInstanceOf(AccessDeniedException.class).hasMessage("관리자만 삭제 가능");
    }
}