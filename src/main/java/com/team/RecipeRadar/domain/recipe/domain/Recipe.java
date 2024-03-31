package com.team.RecipeRadar.domain.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@ToString(includeFieldNames = false, of = {"id", "title","imageUrl" ,"servings", "cookingTime", "cookingLevel"})
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotNull
    private String postNumber;

    //@NotNull
    private String imageUrl;

    //@NotNull
    private  String title;

    // @NotNull
    @Column(length = 3000)
    private String content;

    // @NotNull
    private  String servings;

    //@NotNull
    private String cookingTime;

    //@NotNull
    private  String cookingLevel;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    @Builder
    public Recipe(String postNumber, String imageUrl, String title, String content, String servings, String cookingTime, String cookingLevel, List<Ingredient> ingredients) {
        this.postNumber = postNumber;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.servings = servings;
        this.cookingTime = cookingTime;
        this.cookingLevel = cookingLevel;
        this.ingredients = ingredients;
    }
}
