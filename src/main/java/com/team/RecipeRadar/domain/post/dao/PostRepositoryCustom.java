package com.team.RecipeRadar.domain.post.dao;

import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostRepositoryCustom {

    Slice<UserInfoPostRequest> userInfoPost(Long memberId, Pageable pageable);
}
