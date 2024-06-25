package com.team.RecipeRadar.domain.recipe.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private LocalDate createdAt;

    private CookIngredients cookIngredients;

    private CookMethods cookMethods;

    private DishTypes dishTypes;

    public RecipeDto(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount,LocalDateTime createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.cookingLevel = cookingLevel;
        this.people = people;
        this.cookingTime = cookingTime;
        this.likeCount = likeCount;
        this.createdAt =createdAt.toLocalDate();
    }

    public RecipeDto(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount, List<CookStepDto> cookStep, String ingredient, LocalDate localDate) {
        this.id = id;
        this.imageUrl=imageUrl;
        this.title=title;
        this.cookingLevel= cookingLevel;
        this.people = people;
        this.cookingTime = cookingTime;
        this.likeCount= likeCount;
        this.cookSteps =cookStep;
        this.ingredient = ingredient;
        this.createdAt = localDate;
    }

    public RecipeDto toDto(){
        return new RecipeDto(id,imageUrl,title,cookingLevel,people,cookingTime,likeCount, createdAt.atStartOfDay());
    }

    public static RecipeDto from(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount, LocalDateTime createdAt){
        return new RecipeDto(id,imageUrl,title,cookingLevel,people,cookingTime,likeCount,createdAt);
    }

    public static RecipeDto of(Recipe recipe,String imageUrl,List<CookStepDto> cookStep,String ingredient){
        return new RecipeDto(recipe.getId(), imageUrl, recipe.getTitle(), recipe.getCookingLevel(), recipe.getPeople(), recipe.getCookingTime(), recipe.getLikeCount()
        ,cookStep,ingredient,recipe.getCreatedAt().toLocalDate());
    }

    public static RecipeDto categoryOf(Recipe recipe, String imageUrl){
        return RecipeDto.builder().id(recipe.getId()).title(recipe.getTitle()).likeCount(recipe.getLikeCount()).imageUrl(imageUrl).build();
    }
}
