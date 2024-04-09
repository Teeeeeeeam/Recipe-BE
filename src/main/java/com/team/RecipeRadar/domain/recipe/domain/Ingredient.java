package com.team.RecipeRadar.domain.recipe.domain;


import com.team.RecipeRadar.domain.recipe.dto.RecipeSearchedDto;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Entity
@NoArgsConstructor
@ToString(exclude = {"ingredientAndQuantity"})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String postNumber;


    private  String ingredientTitle;

    @Column(length = 1000)

    private  String ingredientAndQuantity;


    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeSearchedDto getRecipeForSearch() {
        return new RecipeSearchedDto(recipe.getPostNumber(), recipe.getImageUrl(), recipe.getTitle());
    }

    @Builder
    public Ingredient(String ingredientTitle,String ingredientAndQuantity, String postNumber) {
        this.ingredientAndQuantity = ingredientAndQuantity;
        this.ingredientTitle = ingredientTitle;
        this.postNumber = postNumber;
    }
}
