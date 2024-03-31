package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.Getter;

@Getter
public class RecipeSearchedDto {

    private String postNumber;
    private String imageUrl;
    private String title;

    public RecipeSearchedDto(String postNumber, String imageUrl, String title) {
        this.postNumber = postNumber;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public static RecipeSearchedDto of(Recipe recipe) {
        return new RecipeSearchedDto(recipe.getPostNumber(), recipe.getImageUrl(), recipe.getTitle());
    }

}
