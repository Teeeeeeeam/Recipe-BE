package com.team.RecipeRadar.domain.bookmark.dao;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomRecipeBookmarkRepository {

    Slice<RecipeDto> userInfoBookmarks(Long memberId, Long lastId, Pageable pageable);
}
