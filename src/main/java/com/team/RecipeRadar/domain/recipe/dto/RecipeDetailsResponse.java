package com.team.RecipeRadar.domain.recipe.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class RecipeDetailsResponse {

    private RecipeDto recipe;
    private List<String> ingredients;
    private List<Map<String,String>> cookStep;

    private RecipeDetailsResponse(RecipeDto recipeDto, List<String> ingredients,   List<Map<String,String>> cookStep) {
        this.recipe = recipeDto;
        this.ingredients = ingredients;
        this.cookStep = cookStep;
    }

    public static RecipeDetailsResponse of(RecipeDto recipeDto, List<String>  ingredients,    List<Map<String,String>> cookStep){
        return new RecipeDetailsResponse(recipeDto,ingredients,cookStep);
    }
}
