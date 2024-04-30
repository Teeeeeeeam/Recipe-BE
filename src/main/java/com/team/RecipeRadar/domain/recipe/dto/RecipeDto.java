package com.team.RecipeRadar.domain.recipe.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {

    private String id;              // 요리 값

    private String imageUrl;

    private String title;           // 요리제목

    private String cookingLevel;   // 난이도

    private String people;         // 인원 수

    private String cookingTime;     // 요리시간

    private Integer likeCount;      // 좋아요 수


}
