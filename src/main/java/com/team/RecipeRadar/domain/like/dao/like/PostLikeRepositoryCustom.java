package com.team.RecipeRadar.domain.like.dao.like;

import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostLikeRepositoryCustom {

    Slice<UserLikeDto> userInfoLikes(Long memberId,Long postLike_lastId, Pageable pageable);

    void deleteRecipeId(Long recipeId);
}
