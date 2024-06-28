package com.team.RecipeRadar.domain.recipe.application.admin;

import com.team.RecipeRadar.domain.Image.application.ImageService;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.like.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.like.dao.like.RecipeLikeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.bookmark.dao.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.CookStepRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeUpdateRequest;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminRecipeServiceImpl implements AdminRecipeService {

    private final RecipeRepository recipeRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookmarkRepository recipeBookmarkRepository;
    private final CommentRepository commentRepository;
    private final ImageService imageService;
    private final CookStepRepository cookStepRepository;
    private final IngredientRepository ingredientRepository;
    private final S3UploadService s3UploadService;

    /**
     * 레시피를 저장하는 메서드.
     * 이미지 파일과 재료를 저장하고, 요리 단계를 저장합니다.
     */
    @Override
    public void saveRecipe(RecipeSaveRequest recipeSaveRequest, MultipartFile file) {
        Recipe recipe = recipeRepository.save(Recipe.createRecipe(recipeSaveRequest.getTitle(),recipeSaveRequest.getCookTime(),recipeSaveRequest.getCookLevel(), recipeSaveRequest.getPeople(),recipeSaveRequest.getCookIngredients(),recipeSaveRequest.getCookMethods(),recipeSaveRequest.getDishTypes()));
        saveIngredient(recipeSaveRequest, recipe);
        s3UploadService.uploadFile(file,List.of(recipe));
        saveCookingSteps(recipeSaveRequest.getCookSteps(), recipe);
    }

    /**
     * 모든 레시피의 수를 반환하는 메서드.
     */
    @Override
    public long searchAllRecipes() {
        return recipeRepository.countAllBy();
    }

    /**
     * 여러 레시피를 삭제하는 메서드.
     * 주어진 ID 목록에 포함된 모든 레시피를 삭제하고 관련된 데이터들도 함께 삭제합니다.
     */
    @Override
    public void deleteRecipe(List<Long> ids) {
        ids.forEach(this::deleteRecipeById);
    }

    /**
     * 특정 레시피의 정보를 업데이트하는 메서드.
     * 이미지 업로드 처리, 요리 단계 업데이트, 새로운 요리 단계 추가, 삭제할 요리 단계 삭제 등의 작업을 수행합니다.
     */
    @Override
    public void updateRecipe(Long recipeId, RecipeUpdateRequest recipeUpdateRequest, MultipartFile file) {
        Recipe recipe = getRecipeById(recipeId);

        // 파일 업로드 처리
        s3UploadService.updateFile(file,List.of(recipe));

        // 요리 단계 업데이트
        updateCookingSteps(recipeUpdateRequest.getCookSteps());

        // 새로운 요리 단계 추가
        createNewCookingSteps(recipeUpdateRequest.getNewCookSteps(), recipe);

        // 삭제할 요리 단계 삭제
        deleteCookingSteps(recipeUpdateRequest.getDeleteCookStepsId());

        // 재료 업데이트
        updateIngredients(recipeUpdateRequest.getIngredients(), recipe);

        // 레시피 정보 업데이트
        recipe.updateRecipe(recipeUpdateRequest.getTitle(), recipeUpdateRequest.getCookLevel(), recipeUpdateRequest.getPeople(), recipeUpdateRequest.getCookTime(),recipeUpdateRequest.getCookIngredients(),recipeUpdateRequest.getCookMethods(),recipeUpdateRequest.getDishTypes());

    }

    /**
     * 제목과 재료로 레시피를 검색하여 페이징된 결과를 반환하는 메서드.
     * 관리자 페이지에서 사용하는 검색 기능입니다.
     */
    @Override
    @Transactional(readOnly = true)
    public RecipeResponse searchRecipesByTitleAndIngredients(List<String> ingredients, String title, Long lastRecipeId, Pageable pageable) {
        Slice<RecipeDto> recipeSlice = recipeRepository.adminSearchTitleOrIng(ingredients, title, lastRecipeId, pageable);
        return new RecipeResponse(recipeSlice.getContent(), recipeSlice.hasNext());
    }


    // 아래는 private 메서드들입니다.
    /**
     * 특정 ID의 레시피를 삭제하는 메서드.
     * 레시피와 관련된 이미지, 댓글, 좋아요, 북마크 등의 데이터도 함께 삭제합니다.
     */
    private void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_RECIPE));

        // 관련 데이터 삭제
        deleteRecipeRelatedData(recipe.getId());

        // 레시피 삭제
        recipeRepository.deleteById(recipe.getId());
    }

    /**
     * 특정 ID의 레시피와 관련된 데이터를 삭제하는 메서드.
     */
    private void deleteRecipeRelatedData(Long recipeId) {
        imageService.deleteRecipe(recipeId);
        commentRepository.deleteCommentsByRecipeId(recipeId);
        postLikeRepository.deleteRecipeId(recipeId);
        postRepository.deletePostByRecipeId(recipeId);
        recipeLikeRepository.deleteRecipeId(recipeId);
        recipeBookmarkRepository.deleteAllByRecipe_Id(recipeId);
        ingredientRepository.deleteRecipeId(recipeId);
    }

    /**
     * 재료순서 저장 하는 메서드
     * 레시피 저장하 조리 순서에 새로운 순서를 저장 합니다.
     */
    private void saveCookingSteps(List<String> cookSteps, Recipe savedRecipe) {
        List<CookingStep> cookingSteps = cookSteps.stream()
                .map(step -> CookingStep.createCookingStep(savedRecipe, step))
                .collect(Collectors.toList());
        cookStepRepository.saveAll(cookingSteps);
    }

    private void saveIngredient(RecipeSaveRequest recipeSaveRequest, Recipe recipe) {
        String ingredients = getIngredients(recipeSaveRequest.getIngredients());
        ingredientRepository.save(Ingredient.createIngredient(ingredients, recipe));
    }

    /* 재료 리스트를 문자열로 바꾸는 메서드 */
    private static String getIngredients(List<String> recipeSaveRequest) {
        return  recipeSaveRequest.stream().collect(Collectors.joining("|"));
    }

    /**
     * 요리 단계들을 업데이트하는 메서드.
     */
    private void updateCookingSteps(List<Map<String, String>> cookSteps) {
        if (cookSteps != null) {
            cookSteps.forEach(cookStep -> {
                long cookStepId = Long.parseLong(cookStep.get("cookStepId"));
                CookingStep cookingStep = cookStepRepository.findById(cookStepId)
                        .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_COOK_STEP));
                cookingStep.update(cookStep.get("cookSteps"));
            });
        }
    }

    /**
     * 새로운 요리 단계들을 생성하는 메서드.
     */
    private void createNewCookingSteps(List<String> newCookSteps, Recipe recipe) {
        if (newCookSteps != null) {
            newCookSteps.forEach(step -> cookStepRepository.save(CookingStep.createCookingStep(recipe, step)));
        }
    }

    /**
     * 삭제할 요리 단계들을 삭제하는 메서드.
     */
    private void deleteCookingSteps(List<Long> deleteCookStepsIds) {
        if (deleteCookStepsIds != null) {
            deleteCookStepsIds.forEach(cookStepRepository::deleteById);
        }
    }

    /**
     * 재료를 업데이트하는 메서드.
     */
    private void updateIngredients(List<String> ingredients, Recipe recipe) {
        String ingredientString = getIngredients(ingredients);
        ingredientRepository.updateRecipe_ing(recipe.getId(), ingredientString);
    }

    /**
     * 특정 ID의 레시피를 조회하여 반환하는 메서드.
     */
    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_RECIPE));
    }
}