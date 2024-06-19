package com.team.RecipeRadar.domain.recipe.dto.response;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
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
