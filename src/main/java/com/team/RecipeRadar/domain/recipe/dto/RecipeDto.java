package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {

    private Long id;

    private String postNumber;

    private String imageUrl;

    private  String title;

    private String content;

    private  String servings;

    private String cookingTime;

    private  String cookingLevel;

    private Integer likeCount;

    public Recipe toEntity(RecipeDto recipeDto){
        return Recipe.builder()
                .postNumber(recipeDto.getPostNumber())
                .imageUrl(recipeDto.getImageUrl())
                .title(recipeDto.getTitle())
                .content(recipeDto.getContent())
                .servings(recipeDto.getServings())
                .cookingTime(recipeDto.getCookingTime())
                .cookingLevel(recipeDto.getCookingLevel())
                .likeCount(recipeDto.getLikeCount()).build();
    }

    public static RecipeDto from(Recipe recipe){
        return RecipeDto.builder()
                .postNumber(recipe.getPostNumber())
                .imageUrl(recipe.getImageUrl())
                .title(recipe.getTitle())
                .content(recipe.getContent())
                .servings(recipe.getServings())
                .cookingTime(recipe.getCookingTime())
                .cookingLevel(recipe.getCookingLevel())
                .likeCount(recipe.getLikeCount()).build();
    }

}
