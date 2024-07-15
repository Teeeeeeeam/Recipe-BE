package com.team.RecipeRadar.domain.recipe.dto.request;

import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.global.annotations.NotEmptyMapValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "요리 수정 Request")
public class RecipeUpdateRequest {

    @NotEmpty(message = "변경할 레시피의 제목를 입력해주세요")
    @Schema(example = "짜장면")
    private String title;

    @NotEmpty(message = "변경할 레시피의 난이도를 입력해주세요")
    @Schema(example = "중급")
    private String cookLevel;

    @NotEmpty(message = "변경할 레시피의 인원수를 입력해주세요")
    @Schema(example = "1인분")
    private String people;

    @NotEmpty(message = "변경할 레시피의 재료를 입력해주세요")
    @Schema(example = "[\"양파\", \"고기\"]")
    private List<String> ingredients;

    @NotEmpty(message = "변경할 레시피의 시간를 입력해주세요")
    @Schema(example = "1시간")
    private String cookTime;

    @NotEmptyMapValue(message = "변경할 레시피의 조리순서를 입력해주세요")
    @Schema(example = "[{\"cookStepId\":\"185128\", \"cookSteps\":\"양파를 볶는다.\"}]")
    private List<Map<String,String>> cookSteps;

    @Schema(example = "[\"라드를이용해 볶는다.\"]")
    private List<String> newCookSteps;

    @Schema(example = "[\"185135\"]",description = "삭제할 조리순서가 있을때만 사용")
    private List<Long> deleteCookStepsId;

    @NotNull(message = "카테고리를 선택해주세요")
    private CookIngredients cookIngredients;

    @NotNull(message = "카테고리를 선택해주세요")
    private CookMethods cookMethods;

    @NotNull(message = "카테고리를 선택해주세요")
    private DishTypes dishTypes;

}
