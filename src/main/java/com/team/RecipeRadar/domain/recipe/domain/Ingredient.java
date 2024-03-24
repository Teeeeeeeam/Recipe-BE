package com.team.RecipeRadar.domain.recipe.domain;


import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Entity
@NoArgsConstructor
@ToString
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String postNumber;

    private Long recipeId;

    private  String ingredientTitle;

    @Column(length = 1000)

    private  String ingredientAndQuantity;


    @Builder
    public Ingredient(String ingredientTitle,String ingredientAndQuantity, String postNumber, Long recipeId) {
        this.recipeId = recipeId;
        this.ingredientAndQuantity = ingredientAndQuantity;
        this.ingredientTitle = ingredientTitle;
        this.postNumber = postNumber;
    }
}
