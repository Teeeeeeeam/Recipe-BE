package com.team.RecipeRadar.domain.recipe.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
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

    //TODO:레시피 크롤링 완료 후  재료를 가지고 검색할떄 어떤식으로 검색할껀지 결정후 연관관계 결정
    //일단은 재료에서 다대일 맵핑
    //private String recipeIngredients;

  /*  @OneToMany
    @JoinColumn(name = "recipe_id")
    private List<CookingStep> cookingStep*/;

    @Builder
    public Recipe(String postNumber, String imageUrl, String title, String content, String servings, String cookingTime, String cookingLevel, List<CookingStep> cookingStep) {
        this.postNumber = postNumber;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.servings = servings;
        this.cookingTime = cookingTime;
        this.cookingLevel = cookingLevel;
    }
}
