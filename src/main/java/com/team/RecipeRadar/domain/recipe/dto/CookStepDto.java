package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CookStepDto {

    private Long cookStepId;
    private String cookSteps;


    public static CookStepDto of(CookingStep cookingStep){
        return new CookStepDto(cookingStep.getId(),cookingStep.getSteps());
    }
}
