package com.team.RecipeRadar.domain.recipe.dto.response;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class RecipeDetailsResponse {

    private RecipeDto recipe;
    private List<String> ingredients;
    private List<Map<String,String>> cookSteps;

    private RecipeDetailsResponse(RecipeDto recipeDto, List<String> ingredients,   List<Map<String,String>> cookStep) {
        this.recipe = recipeDto;
        this.ingredients = ingredients;
        this.cookSteps = cookStep;
    }

    public static RecipeDetailsResponse of(RecipeDto recipeDto, List<String>  ingredients,    List<Map<String,String>> cookStep){
        return new RecipeDetailsResponse(recipeDto,ingredients,cookStep);
    }
}
