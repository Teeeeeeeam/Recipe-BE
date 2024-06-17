package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
        return new RecipeNormalPageResponse(recipeDtoPage.getContent(),recipeDtoPage.getTotalPages(),recipeDtoPage.getTotalElements());
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

        List<Map<String, String>> cookList = convertCookStepsToMap(recipeDetails.getCookSteps());
        List<String> ingredients = convertIngredientStringToList(recipeDetails.getIngredient());

        return RecipeDetailsResponse.of(recipeDetails.toDto(), ingredients, cookList);
    }

    @Override
    @Transactional(readOnly = true)
    public MainPageRecipeResponse mainPageRecipe() {
        List<RecipeDto> recipeDtoList = recipeRepository.mainPageRecipe();
        return MainPageRecipeResponse.of(recipeDtoList);
    }

    /* Josn 형식으로 Map으로 담아서 변경 */
    private List<Map<String, String>> convertCookStepsToMap(List<CookStepDto> cookSteps) {
        return cookSteps.stream()
                .map(cookStepDto -> {
                    Map<String, String> cookStepMap = new LinkedHashMap<>();
                    cookStepMap.put("cookStepId", String.valueOf(cookStepDto.getCookStepId()));
                    cookStepMap.put("cookSteps", cookStepDto.getCookSteps());
                    return cookStepMap;
                })
                .collect(Collectors.toList());
    }

    /* 문자열의 | 기준으로 리스트를 만드는 메서드*/
    private List<String> convertIngredientStringToList(String ingredient) {
        return Arrays.stream(ingredient.split("\\|"))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
