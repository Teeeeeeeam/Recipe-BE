package com.team.RecipeRadar.domain.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeResponse {

    private List<RecipeDto> recipeDtoList;

    private Boolean nextPage;

}
