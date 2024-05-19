package com.team.RecipeRadar.domain.post.dao;

import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.user.PostDetailResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostRepositoryCustom {

    Slice<UserInfoPostRequest> userInfoPost(Long memberId, Pageable pageable);

    Slice<PostDto> getAllPost(Pageable pageable);

    PostDetailResponse postDetails(Long postId);
}
