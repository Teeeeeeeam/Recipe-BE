package com.team.RecipeRadar.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRecipeResponse {

    private String recipeId;         // 레시피아이디
    private String imgUrl;          // 이미지 url
    private String title;           // 레피시 제목
    private String people;          // 인원수
    private String cookingLevel;    //요리 난이다
}
