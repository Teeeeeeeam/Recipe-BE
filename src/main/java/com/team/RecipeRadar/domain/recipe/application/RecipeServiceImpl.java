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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final CookStepRepository cookStepRepository;
    private final ImgRepository imgRepository;
    private final S3UploadService s3UploadService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    /**
     * recipeRepository에서 페이징쿼리를 담아 반환된 데이터를 Response로 옮겨담아 전송, 조회 전용 메소드
     * @param ingredients       재료 리스트값
     * @param pageable          페이징 (sort x)
     * @return                  RecipeResponse 객체로 반환
     */
    @Override
    @Transactional(readOnly = true)
    public RecipeResponse searchRecipesByIngredients(List<String> ingredients,Long lastRecipeId, Pageable pageable) {

        Slice<RecipeDto> recipe = recipeRepository.getRecipe(ingredients,lastRecipeId, pageable);

        return new RecipeResponse(recipe.getContent(),recipe.hasNext());
    }

    /*
    searchRecipesByIngredients와 검색 기능은 동일하나 해당 로직은 admin 사용자를 위한 검색 api
     */
    @Override
    @Transactional(readOnly = true)
    public RecipeResponse searchRecipesByTitleAndIngredients(List<String> ingredients,String title, Long lastRecipeId, Pageable pageable) {
        Slice<RecipeDto> recipe = recipeRepository.adminSearchTitleOrIng(ingredients,title,lastRecipeId, pageable);
        return new RecipeResponse(recipe.getContent(),recipe.hasNext());
    }

    @Override
    public  Page<RecipeDto> searchRecipeByIngredientsNormal(List<String> ingredients,String title, Pageable pageable) {
        return  recipeRepository.getNormalPage(ingredients, title,pageable);
    }

    /**
     * 레시피의 상세정보를 보는 로직,
     * @param recipeId  찾을 레시피 번호
     * @return      Response로 변환해 해당 레시피의 상세 정보를 반환
     */
    @Override
    @Transactional(readOnly = true)
    public RecipeDetailsResponse getRecipeDetails(Long recipeId) {
        RecipeDto recipeDetails = recipeRepository.getRecipeDetails(recipeId);

        List<Map<String,String>> cookList = new ArrayList<>();
        List<CookingStep> cookingSteps = recipeDetails.getCookingSteps();

        for (CookingStep cookingStep : cookingSteps){
            Map<String, String> cookStepMap = new LinkedHashMap<>(); // 새로운 Map 객체 생성

            cookStepMap.put("cook_step_id", String.valueOf(cookingStep.getId()));
            cookStepMap.put("cook_steps", cookingStep.getSteps());

            cookList.add(cookStepMap); // 새로운 Map 객체를 리스트에 추가
        }


        String ingredient = recipeDetails.getIngredient();
        StringTokenizer st = new StringTokenizer(ingredient, "|");
        List<String> ingredients =new ArrayList<>();

        while (st.hasMoreTokens()){                     // 문자열로 저장된 레시시피 데이터를 | 기준으로 데이터를 배열로 변환
            String ingred_token = st.nextToken();
            if (ingred_token.charAt(0) == ' ') {        // 첫번째 인덱스가 빈 공간일때
                ingred_token = ingred_token.substring(1);       // 다음 인덱스부터 출력
            }
            ingredients.add(ingred_token);
        }

        return RecipeDetailsResponse.of(recipeDetails.toDto(),ingredients,cookList);
    }

    @Override
    @Transactional(readOnly = true)
    public MainPageRecipeResponse mainPageRecipe() {
        List<RecipeDto> recipeDtoList = recipeRepository.mainPageRecipe();
        return MainPageRecipeResponse.of(recipeDtoList);
    }

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

        List<Map<String, String>> cookeSteps = recipeUpdateRequest.getCookeSteps();

        for (Map<String,String> cookeStep : cookeSteps) {
            long cookStepId = Long.parseLong(cookeStep.get("cook_step_id"));
            Optional<CookingStep> byId = cookStepRepository.findById(cookStepId);
            if (byId.isPresent()){
                String cookSteps_Value = cookeStep.get("cook_steps");
                CookingStep cookingStep = byId.get();
                cookingStep.update(cookSteps_Value);
                cookStepRepository.save(cookingStep);
            }
        }

        String ing = recipeUpdateRequest.getIngredients().stream().collect(Collectors.joining("|"));
        ingredientRepository.updateRecipe_ing(recipe.getId(),ing);

        recipe.s3_update_recipe(recipeUpdateRequest.getTitle(),recipeUpdateRequest.getCookLevel(),recipeUpdateRequest.getPeople(),recipeUpdateRequest.getCookTime());
        recipeRepository.save(recipe);
    }


    /**
     * 관리자만 레시피를 삭제 가능 레시피와 관련된 모든 데이터 삭제
     * @param recipeId
     * @param loginId
     */
    @Override
    public void deleteByAdmin(Long recipeId, String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        if(!member.getRoles().equals("ROLE_ADMIN")){
            throw new AccessDeniedException("관리지만 삭제 가능합니다.");
        }

        UploadFile uploadFile = imgRepository.findrecipeIdpostNull(recipeId).get();

        String storeFileName = uploadFile.getStoreFileName();
        postRepository.deleteAllByRecipe_Id(recipeId);
        s3UploadService.deleteFile(storeFileName);
        ingredientRepository.deleteRecipeId(recipeId);
        imgRepository.deleteRecipeId(recipeId);
        cookStepRepository.deleteRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);
    }

}
