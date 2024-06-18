package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "요리순서 DTO")
@Data
@AllArgsConstructor
public class CookStepDto {

    private Long cookStepId;
    private String cookSteps;


    public static CookStepDto of(CookingStep cookingStep){
        return new CookStepDto(cookingStep.getId(),cookingStep.getSteps());
    }
}
