package com.team.RecipeRadar.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecipeNormalPageResponse {

    private List<RecipeDto> recipes;
    private int totalPage;
    private long totalElements;
}
