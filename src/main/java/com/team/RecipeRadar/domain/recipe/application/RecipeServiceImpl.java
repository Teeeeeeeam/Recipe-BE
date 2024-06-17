package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{

    private final RecipeRepository recipeRepository;

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

    @Override
    @Transactional(readOnly = true)
    public RecipeNormalPageResponse searchRecipeByIngredientsNormal(List<String> ingredients,String title, Pageable pageable) {
        Page<RecipeDto> recipeDtoPage = recipeRepository.getNormalPage(ingredients, title, pageable);
        return new RecipeNormalPageResponse(recipeDtoPage.getContent(),recipeDtoPage.getTotalPages());
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

}
