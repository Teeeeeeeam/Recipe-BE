package com.team.RecipeRadar.domain.recipe.domain;

import com.team.RecipeRadar.domain.recipe.dto.RecipeSaveRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = false, of = {"title", "cookingTime", "cookingLevel"})
@Table(indexes = {
        @Index(columnList = "likeCount"),
        @Index(columnList = "recipe_title")
})
@Schema(hidden = true, name = "레시피")
public class Recipe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;              // 요리 값

    @Column(name = "recipe_title")
    private String title;           // 요리제목

    private String cookingLevel;   // 난이도

    @Column(name = "recipe_servings")
    private String people;         // 인원 수

    private String cookingTime;     // 요리시간

    private Integer likeCount;      // 좋아요 수


    @OneToMany(mappedBy = "recipe",cascade = CascadeType.ALL)
    private List<CookingStep> cookingStepList=  new ArrayList<>();

    public void setLikeCount(int count){            //좋아요 증가 set
        this.likeCount = count;
    }

    public void updateRecipe(String title, String cookingLevel, String people, String cookingTime){
        this.title=title;
        this.cookingLevel=cookingLevel;
        this.people=people;
        this.cookingTime=cookingTime;
    }
    public static Recipe toEntity(RecipeSaveRequest recipeSaveRequest){
        return  Recipe.builder()
                .title(recipeSaveRequest.getTitle())
                .cookingTime(recipeSaveRequest.getCookTime())
                .cookingLevel(recipeSaveRequest.getCookLevel())
                .likeCount(0)
                .people(recipeSaveRequest.getPeople()).build();
    }
}
