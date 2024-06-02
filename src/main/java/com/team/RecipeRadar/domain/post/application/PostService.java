package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    void save(UserAddRequest userAddPostDto, MultipartFile file);

    PostResponse postPage(Long postId,Pageable pageable);

    Post findById(long id);

    void delete(String loginId, Long postId);

    PostDetailResponse postDetail(Long postId);
    void update(Long postId,UserUpdateRequest userUpdatePostDto,String loginId,MultipartFile file);

    UserInfoPostResponse userPostPage(String authenticationName,Long lastId, String loginId, Pageable pageable);

    boolean validPostPassword(String login, ValidPostRequest request);

    PostResponse searchPost(String loginId, String recipeTitle, String postTitle ,Long lastPostId, Pageable pageable);

    void deletePosts(List<Long> postIds);
}
