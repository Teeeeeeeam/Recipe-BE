package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;
import com.team.RecipeRadar.domain.post.dto.user.UserAddPostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserDeletePostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    Post save_post(UserAddPostDto userAddPostDto);

    List<Post> findAll();

    Post findById(long id);

    void delete_post(UserDeletePostDto userDeletePostDto);


    void update_post(Long memeber_id, Long post_id, String postTitle, String postContent);


}
