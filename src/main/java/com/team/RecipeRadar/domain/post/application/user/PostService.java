package com.team.RecipeRadar.domain.post.application.user;

import com.team.RecipeRadar.domain.post.dto.request.UserAddRequest;
import com.team.RecipeRadar.domain.post.dto.request.UserUpdateRequest;
import com.team.RecipeRadar.domain.post.dto.request.ValidPostRequest;
import com.team.RecipeRadar.domain.post.dto.response.*;
import com.team.RecipeRadar.domain.post.dto.response.UserInfoPostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface PostService {
    void save(UserAddRequest userAddRequest, Long memberId, MultipartFile file);

    PostResponse postPage(Long postId,Pageable pageable);

    void delete(Long memberId, Long postId);

    PostDetailResponse postDetail(Long postId);

    void update(Long postId, Long memberId , UserUpdateRequest userUpdateRequest, MultipartFile file);

    UserInfoPostResponse userPostPage(Long memberId,Long lastId, Pageable pageable);

    void validPostPassword(Long memberId, ValidPostRequest validPostRequest);

    RecipeLikeTopPostResponse getTop4RecipesByLikes(Long recipeId);

    RecipeLikeTopPostResponse getTopMainsByLikes();
}
