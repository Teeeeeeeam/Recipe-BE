package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService{

    long searchAllPosts();

    long searchAllRecipes();

    PostsCommentResponse getPostsComments(Long postId,Long lastId,Pageable pageable);

    void deleteComments(List<Long> ids);

    void deleteRecipe(List<Long> ids);

}
