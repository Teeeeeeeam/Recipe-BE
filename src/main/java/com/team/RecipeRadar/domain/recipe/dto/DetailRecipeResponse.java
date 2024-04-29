package com.team.RecipeRadar.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailRecipeResponse {

    private String recipeId;        // 레시피 아이디
    private String recipeTitle;    // 레시피 명
    private String cookLevel;      //요리 난이도
    private String people;          //인원수
    private String cookTime;        //요리시간
    private List<String> ingredients;       // 재료
    private List<String> steps;         //요리순서

}
