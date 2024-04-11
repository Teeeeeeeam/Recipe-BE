package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.user.UserAddPostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserDeletePostDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    Post save(UserAddPostDto userAddPostDto);

    List<Post> findAll();

    Post findById(long id);

    void delete(UserDeletePostDto userDeletePostDto);


    void update(Long memberId, Long postId, String postTitle, String postContent,
                String postServing, String postCookingTime, String postCookingLevel, String postImageUrl);

    UserInfoPostResponse userPostPage(String authenticationName, String loginId, Pageable pageable);


}
