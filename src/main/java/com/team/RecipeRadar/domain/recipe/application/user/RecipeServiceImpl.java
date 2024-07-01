package com.team.RecipeRadar.domain.recipe.application.user;

import com.team.RecipeRadar.domain.recipe.api.user.OrderType;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.domain.recipe.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{

    private final RecipeRepository recipeRepository;

    @Override
    @Transactional(readOnly = true)
    public RecipeSearchResponse searchRecipeByIngredientsNormal(List<String> ingredients, List<CookIngredients> cookIngredients, List<CookMethods> cookMethods, List<DishTypes> dishTypes, String title,
                                                                OrderType order, Integer likeCount, Long lastId, Pageable pageable) {
        Slice<RecipeDto> recipeDtoSlice = recipeRepository.userSearchRecipe(ingredients, cookIngredients,cookMethods,dishTypes,title,order,likeCount, lastId,pageable);
        return new RecipeSearchResponse(recipeDtoSlice.hasNext(), recipeDtoSlice.getContent());
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
