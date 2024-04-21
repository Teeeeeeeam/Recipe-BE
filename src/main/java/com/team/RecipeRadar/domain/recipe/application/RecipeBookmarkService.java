package com.team.RecipeRadar.domain.recipe.application;

public interface RecipeBookmarkService {

    Boolean saveBookmark(Long memberId, Long recipeId);
}
