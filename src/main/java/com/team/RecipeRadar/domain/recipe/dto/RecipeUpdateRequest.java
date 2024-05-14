package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.dto.annotations.NotEmptyMapValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeUpdateRequest {

    @NotEmpty(message = "변경할 레시피의 제목를 입력해주세요")
    private String title;
    @NotEmpty(message = "변경할 레시피의 난이도를 입력해주세요")
    private String cookLevel;
    @NotEmpty(message = "변경할 레시피의 인원수를 입력해주세요")
    private String people;
    @NotEmpty(message = "변경할 레시피의 재료를 입력해주세요")
    @Schema(example = "{\"ingredients\":[\"재료1\", \"재료2\"]}")
    private List<String> ingredients;
    @NotEmpty(message = "변경할 레시피의 시간를 입력해주세요")
    private String cookTime;
    @NotEmptyMapValue(message = "변경할 레시피의 조리순서를 입력해주세요")
    @Schema(example = "[{\"cook_step_id\":\"조리순서_id\", \"cook_steps\":\"조리순서\"}]")
    private List<Map<String,String>> cookeSteps;


    private RecipeUpdateRequest(String title, String cookLevel, String people, List<String>  ingredients, List<Map<String,String>> cookeSteps) {
        this.title = title;
        this.cookLevel = cookLevel;
        this.people = people;
        this.ingredients = ingredients;
        this.cookeSteps = cookeSteps;

    }

    public static RecipeUpdateRequest of(RecipeUpdateRequest recipeUpdateRequest){
        return new RecipeUpdateRequest(recipeUpdateRequest.getTitle(),recipeUpdateRequest.getCookLevel(),recipeUpdateRequest.getPeople(),recipeUpdateRequest.getIngredients(),recipeUpdateRequest.getCookeSteps());
    }
}
