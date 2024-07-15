package com.team.RecipeRadar.domain.bookmark.application;

import com.team.RecipeRadar.domain.bookmark.dto.response.UserInfoBookmarkResponse;
import org.springframework.data.domain.Pageable;

public interface RecipeBookmarkService {

    Boolean saveBookmark(Long memberId, Long recipeId);

    Boolean checkBookmark(Long memberId,Long recipeId);

    UserInfoBookmarkResponse userInfoBookmark(Long memberId, Long lastId, Pageable pageable);

}
