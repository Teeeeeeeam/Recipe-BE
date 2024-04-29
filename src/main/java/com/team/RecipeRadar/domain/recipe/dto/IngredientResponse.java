package com.team.RecipeRadar.domain.recipe.dto;

import lombok.Data;

import java.util.List;

@Data
public class IngredientResponse {

    String title;                  // 요리 제목
    List<String> ingredients;      // 요리 재료  
    List<String> steps;            // 요리 순서
}
