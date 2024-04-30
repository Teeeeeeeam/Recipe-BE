package com.team.RecipeRadar.domain.recipe.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = false, of = {"title","imageUrl" , "cookingTime", "cookingLevel"})
@Table(indexes = {
        @Index(columnList = "likeCount"),
        @Index(columnList = "title")
})
public class Recipe {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;              // 요리 값

    @Setter
    private String imageUrl;

    private String title;           // 요리제목

    private String cookingLevel;   // 난이도

    private String people;         // 인원 수

    private String cookingTime;     // 요리시간

    private Integer likeCount;      // 좋아요 수


    public void setLikeCount(int count){            //좋아요 증가 set
        this.likeCount = count;
    }
}
