package com.team.RecipeRadar.domain.recipe.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id", updatable = false)
    private Long id;

    @Column(name = "recipe_title", nullable = false)
    private String recipeTitle;

    @Column(name = "recipe_content", nullable = false)
    private String recipeContent;

    @Column(name = "recipe_serving", nullable = false)
    private String recipeServing;

    @Column(name = "cooking_time", nullable = false)
    private String cookingTime;

    @Column(name = "ingredients_amount", nullable = false)
    private String ingredientsAmount;

    @Column(name = "cooking_step", nullable = false)
    private String cookingStep;

    @Column(name = "recipe_level", nullable = false)
    private String recipeLevel;

    private Integer likeCount;      // 좋아요 수


    @Builder
    public Recipe(String recipeTitle, String recipeContent, String recipeServing, String cookingTime, String ingredientsAmount, String cookingStep, String recipeLevel,int count) {
        this.recipeTitle = recipeTitle;
        this.recipeContent = recipeContent;
        this.recipeServing = recipeServing;
        this.cookingTime = cookingTime;
        this.ingredientsAmount = ingredientsAmount;
        this.cookingStep = cookingStep;
        this.recipeLevel = recipeLevel;
        this.likeCount = count;

    }

    public void update(String recipeTitle, String recipeContent, String recipeServing, String cookingTime, String ingredientsAmount, String cookingStep, String recipeLevel) {
        this.recipeTitle = recipeTitle;
        this.recipeContent = recipeContent;
        this.recipeServing = recipeServing;
        this.cookingTime = cookingTime;
        this.ingredientsAmount = ingredientsAmount;
        this.cookingStep = cookingStep;
        this.recipeLevel = recipeLevel;
    }

    public void setLikeCount(int count){
        this.likeCount = count;
    }
}
