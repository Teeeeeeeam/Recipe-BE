package com.team.RecipeRadar.domain.recipe.application;

public interface RecipeBookmarkService {

    Boolean saveBookmark(Long memberId, Long recipeId);

    Boolean checkBookmark(Long memberId,Long recipeId);
}
