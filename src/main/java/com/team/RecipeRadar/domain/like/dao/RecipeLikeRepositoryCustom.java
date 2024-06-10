package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RecipeLikeRepositoryCustom {

    Slice<UserLikeDto> userInfoRecipeLikes(Long memberId,Long recipeLike_lastId ,Pageable pageable);

    void deleteRecipeId(Long recipeId);
}
