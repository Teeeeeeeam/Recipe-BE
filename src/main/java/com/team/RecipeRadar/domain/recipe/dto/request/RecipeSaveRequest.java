package com.team.RecipeRadar.domain.recipe.dto.request;

import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "레시피 작성 Request")
public class RecipeSaveRequest {

    @Schema(example = "짜장면")
    @NotEmpty(message = "레시피 제목을 작성해주세요")
    private String title;

    @Schema(example = "중")
    @NotEmpty(message = "레시피의 난이도를 작성해주세요")
    private String cookLevel;

    @Schema(example = "1인분")
    @NotEmpty(message = "레시피의 인원수를 작성해주세요")
    private String people;

    @Schema(example = "[\"양파\",\"고기\",\"춘장\"]")
    @NotEmpty(message = "레시피의 재료를 입력해주세여")
    private List<String> ingredients;

    @Schema(example = "30분")
    @NotEmpty(message = "레시피의 조리 시간을 작성해주세요")
    private String cookTime;

    @Schema(example = "[\"양파를 볶는다.\" , \"면을 삶는다.\"]")
    @NotEmpty(message = "레시피의 조리 순서를 작성해주세요")
    private List<String> cookSteps;

    @NotNull(message = "주재료 카테고리를 선택해주세요")
    private CookIngredients cookIngredients;

    @NotNull(message = "대표 조리 카테고리를 선택해주세요")
    private CookMethods cookMethods;

    @NotNull(message = "레시피 타입 카테고리를 선택해주세요")
    private DishTypes dishTypes;

    private RecipeSaveRequest(String title, String cookLevel, String people, List<String> ingredients, List<String> cookSteps,CookIngredients cookIngredients, CookMethods cookMethods, DishTypes dishTypes) {
        this.title = title;
        this.cookLevel = cookLevel;
        this.people = people;
        this.ingredients = ingredients;
        this.cookSteps = cookSteps;
        this.cookIngredients=  cookIngredients;
        this.cookMethods = cookMethods;
        this.dishTypes = dishTypes;
    }

    public static RecipeSaveRequest of(RecipeSaveRequest recipeSaveRequest){
        return new RecipeSaveRequest(recipeSaveRequest.getTitle(),recipeSaveRequest.getCookLevel(),recipeSaveRequest.getPeople(),recipeSaveRequest.getIngredients(),recipeSaveRequest.getCookSteps(),recipeSaveRequest.getCookIngredients(),
                recipeSaveRequest.getCookMethods(),recipeSaveRequest.getDishTypes());
    }
}
