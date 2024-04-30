package com.team.RecipeRadar.domain.recipe.dto;


import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class RecipeDto {

    private Long id;              // 요리 값

    private String imageUrl;

    private String title;           // 요리제목

    private String cookingLevel;   // 난이도

    private String people;         // 인원 수

    private String cookingTime;     // 요리시간

    private Integer likeCount;      // 좋아요 수


    public RecipeDto(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.cookingLevel = cookingLevel;
        this.people = people;
        this.cookingTime = cookingTime;
        this.likeCount = likeCount;
    }

    public static RecipeDto of(Long id, String imageUrl, String title, String cookingLevel, String people, String cookingTime, Integer likeCount){
        return new RecipeDto(id,imageUrl,title,cookingLevel,people,cookingTime,likeCount);
    }

}
