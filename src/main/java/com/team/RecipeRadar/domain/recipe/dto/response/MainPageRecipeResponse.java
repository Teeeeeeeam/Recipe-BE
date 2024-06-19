package com.team.RecipeRadar.domain.recipe.dto.response;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.Data;

import java.util.List;

@Data
public class MainPageRecipeResponse {

    private List<RecipeDto> recipe;

    private MainPageRecipeResponse(List<RecipeDto> list) {
        this.recipe = list;
    }

    public static MainPageRecipeResponse of(List<RecipeDto> list){
        return new MainPageRecipeResponse(list);
    }
}
