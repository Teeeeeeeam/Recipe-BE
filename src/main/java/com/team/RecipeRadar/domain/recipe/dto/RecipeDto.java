package com.team.RecipeRadar.domain.recipe.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "레시피 DTO")
public class RecipeDto {

    private Long id;              // 요리 값

    private String imageUrl;

    private String title;           // 요리제목

    private String cookingLevel;   // 난이도

    private String people;         // 인원 수

    private String cookingTime;     // 요리시간

    private Integer likeCount;      // 좋아요 수

    private List<CookStepDto> cookSteps;         // 조리순서

    private String ingredient;          // 재료


    public RecipeDto(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.cookingLevel = cookingLevel;
        this.people = people;
        this.cookingTime = cookingTime;
        this.likeCount = likeCount;
    }

    public RecipeDto toDto(){
        return new RecipeDto(id,imageUrl,title,cookingLevel,people,cookingTime,likeCount);
    }

    public static RecipeDto from(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount){
        return new RecipeDto(id,imageUrl,title,cookingLevel,people,cookingTime,likeCount);
    }

    public static RecipeDto of(Recipe recipe,String imageUrl,List<CookStepDto> cookStep,String ingredient){
        return new RecipeDto(recipe.getId(), imageUrl, recipe.getTitle(), recipe.getCookingLevel(), recipe.getPeople(), recipe.getCookingTime(), recipe.getLikeCount()
        ,cookStep,ingredient);
    }
}
