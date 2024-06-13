package com.team.RecipeRadar.domain.admin.application.recipe;

import com.team.RecipeRadar.domain.Image.application.ImageService;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.like.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.CookStepRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.RecipeUpdateRequest;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
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
    private final ImgRepository imgRepository;
    private final IngredientRepository ingredientRepository;
    private final S3UploadService s3UploadService;

    /**
     * 레시피 저장하는 로직 s3에 이미지 저장
     */

    @Override
    public void saveRecipe(RecipeSaveRequest recipeSaveRequest, String fileUrl, String originalFilename) {

        Recipe save_Recipe= recipeRepository.save(Recipe.toEntity_s3(recipeSaveRequest));

        String ingredient_stream = recipeSaveRequest.getIngredients().stream().collect(Collectors.joining("|"));

        UploadFile uploadFile = UploadFile.builder().storeFileName(fileUrl).originFileName(originalFilename).recipe(save_Recipe).build();
        imgRepository.save(uploadFile);

        Ingredient ingredient = Ingredient.builder()
                .ingredients(ingredient_stream)
                .recipe(save_Recipe).build();

        ingredientRepository.save(ingredient);

        List<String> cookSteps = recipeSaveRequest.getCookSteps();
        List<CookingStep> cookingSteps = new ArrayList<>();
        for (String steps : cookSteps){
            cookingSteps.add(CookingStep.builder().steps(steps).recipe(save_Recipe).build());
        }
        cookStepRepository.saveAll(cookingSteps);
    }


    @Override
    public long searchAllRecipes() {
        return recipeRepository.countAllBy();
    }


    @Override
    public void deleteRecipe(List<Long> ids) {
        for (Long id : ids) {
            deleteRecipeById(id);
        }
    }

    private void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("해당 레시피를 찾을수 없습니다."));
        Long recipeId = recipe.getId();

        imageService.delete_Recipe(recipeId);
        commentRepository.delete_post(recipeId);
        postLikeRepository.deleteRecipeId(recipeId);
        postRepository.deletePostByRecipeId(recipeId);
        recipeLikeRepository.deleteRecipeId(recipeId);
        recipeBookmarkRepository.deleteAllByRecipe_Id(recipeId);
        ingredientRepository.deleteRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);
    }

    /**
     * 레시피의 정보를 수정하는 로직 해당 이미지파일의 정보가 db에 저장된 이미와 같을 경우 이미지 저장 로직 실행되지 않음
     * @param recipeId
     * @param recipeUpdateRequest
     * @param file
     */
    @Override
    public void updateRecipe(Long recipeId, RecipeUpdateRequest recipeUpdateRequest, MultipartFile file) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NoSuchElementException("해당 레시피를 찾을수 없습니다."));

        UploadFile uploadFile = imgRepository.findrecipeIdpostNull(recipeId).get();

        if(file!=null) {
            if (!uploadFile.getOriginFileName().equals(file.getOriginalFilename())) {
                s3UploadService.deleteFile(uploadFile.getStoreFileName());
                String storedFile = s3UploadService.uploadFile(file);
                uploadFile.update(storedFile, file.getOriginalFilename());
                imgRepository.save(uploadFile);
            }
        }

        List<Map<String, String>> cookeSteps = recipeUpdateRequest.getCookSteps();

        if(cookeSteps!=null) {
            for (Map<String, String> cookeStep : cookeSteps) {
                long cookStepId = Long.parseLong(cookeStep.get("cook_step_id"));
                Optional<CookingStep> byId = cookStepRepository.findById(cookStepId);
                if (byId.isPresent()) {
                    String cookSteps_Value = cookeStep.get("cook_steps");
                    CookingStep cookingStep = byId.get();
                    cookingStep.update(cookSteps_Value);
                    cookStepRepository.save(cookingStep);
                }
            }
        }
        List<String> newCookSteps = recipeUpdateRequest.getNewCookSteps();

        if (newCookSteps != null){
            CookingStep.CookingStepBuilder recipe1 = CookingStep.builder().recipe(recipe);
            newCookSteps.stream().forEach(s -> recipe1.steps(s).build());
            cookStepRepository.save(recipe1.build());
        }

        if(recipeUpdateRequest.getDeleteCookStepsId()!=null){
            recipeUpdateRequest.getDeleteCookStepsId().stream().forEach(s ->cookStepRepository.deleteById(s));
        }

        String ing = recipeUpdateRequest.getIngredients().stream().collect(Collectors.joining("|"));
        ingredientRepository.updateRecipe_ing(recipe.getId(),ing);

        recipe.s3_update_recipe(recipeUpdateRequest.getTitle(),recipeUpdateRequest.getCookLevel(),recipeUpdateRequest.getPeople(),recipeUpdateRequest.getCookTime());
        recipeRepository.save(recipe);
    }

    /*
   searchRecipesByIngredients와 검색 기능은 동일하나 해당 로직은 admin 사용자를 위한 검색 api
    */
    @Override
    @Transactional(readOnly = true)
    public RecipeResponse searchRecipesByTitleAndIngredients(List<String> ingredients, String title, Long lastRecipeId, Pageable pageable) {
        Slice<RecipeDto> recipe = recipeRepository.adminSearchTitleOrIng(ingredients,title,lastRecipeId, pageable);
        return new RecipeResponse(recipe.getContent(),recipe.hasNext());
    }
}
