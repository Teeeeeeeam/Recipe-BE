package com.team.RecipeRadar.domain.recipe.domain;

import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.global.utils.BaseTimeUtils;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "like_count"),
        @Index(columnList = "recipe_title"),
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = false, of = {"title", "cookingTime", "cookingLevel"})
public class Recipe extends BaseTimeUtils {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;              // 요리 값

    @Column(name = "recipe_title")
    private String title;           // 요리제목

    private String cookingLevel;   // 난이도

    @Column(name = "recipe_servings")
    private String people;         // 인원 수

    private String cookingTime;     // 요리시간

    @Column(name = "like_count")
    private Integer likeCount;      // 좋아요 수

    @Enumerated(EnumType.STRING)
    private CookMethods cookMethods; // 요리 방법

    @Enumerated(EnumType.STRING)
    private CookIngredients cookingIngredients; // 요리재료 별명

    @Enumerated(EnumType.STRING)
    private DishTypes types; // 요리 종류 별명


    @OneToMany(mappedBy = "recipe",cascade = CascadeType.ALL)
    private List<CookingStep> cookingStepList=  new ArrayList<>();

    public void setLikeCount(int count){            //좋아요 증가 set
        this.likeCount = count;
    }

    public void updateRecipe(String title, String cookingLevel, String people, String cookingTime,CookIngredients cookIngredients, CookMethods cookMethods, DishTypes dishTypes){
        this.title=title;
        this.cookingLevel=cookingLevel;
        this.people=people;
        this.cookingTime=cookingTime;
        this.cookingIngredients = cookIngredients;
        this.cookMethods = cookMethods;
        this.types = dishTypes;
    }
    public static Recipe createRecipe(String title,String cookingTime, String cookingLevel, String people,CookIngredients cookIngredients, CookMethods cookMethods, DishTypes dishTypes){
        return  Recipe.builder()
                .title(title)
                .cookingTime(cookingTime)
                .cookingLevel(cookingLevel)
                .cookingIngredients(cookIngredients)
                .cookMethods(cookMethods)
                .types(dishTypes)
                .likeCount(0)
                .people(people).build();
    }
}
