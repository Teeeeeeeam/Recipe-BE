package com.team.RecipeRadar.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecipeNormalPageResponse {

    List<RecipeDto> recipes;
    int totalPage;
}
