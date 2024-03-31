package com.team.RecipeRadar.domain.recipe.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;

@Entity
@NoArgsConstructor
public class CookingStep {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postNumber;
    private Integer stepNumber;

    private Long recipeId;
    @Column(length = 3000)
    private String explanation;

    private String imageUrl;


    @Builder
    public CookingStep(String postNumber, Integer stepNumber, String explanation, String imageUrl, Long recipeId) {
        this.recipeId = recipeId;
        this.postNumber = postNumber;
        this.stepNumber = stepNumber;
        this.explanation = explanation;
        this.imageUrl = imageUrl;
    }
}
