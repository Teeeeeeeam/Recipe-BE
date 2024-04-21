package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeBookmarkDto {

    private Long id;

    private MemberDto memberDto;

    private RecipeDto recipeDto;

}
