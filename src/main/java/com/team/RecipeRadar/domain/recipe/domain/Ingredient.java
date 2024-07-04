package com.team.RecipeRadar.domain.recipe.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    @Column(length = 4000)
    @Setter
    private String ingredients;         // 재료

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public static Ingredient createIngredient(String ingredients, Recipe recipe){
        return Ingredient.builder().ingredients(ingredients).recipe(recipe).build();
    }
}