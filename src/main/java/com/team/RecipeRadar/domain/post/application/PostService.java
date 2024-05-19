package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import org.springframework.data.domain.Pageable;

public interface PostService {
    void save(UserAddRequest userAddPostDto);

    PostResponse postPage(Pageable pageable);

    Post findById(long id);

    void delete(String loginId, Long postId);

    PostDetailResponse postDetail(Long postId);
    void update(UserUpdateRequest userUpdatePostDto,String loginId);

    UserInfoPostResponse userPostPage(String authenticationName, String loginId, Pageable pageable);

    boolean validPostPassword(String login, ValidPostRequest request);
}
