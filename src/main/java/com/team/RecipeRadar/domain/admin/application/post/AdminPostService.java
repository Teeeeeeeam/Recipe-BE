package com.team.RecipeRadar.domain.admin.application.post;

import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.post.dto.user.PostResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminPostService {

    long searchAllPosts();

    PostsCommentResponse getPostsComments(Long postId, Long lastId, Pageable pageable);

    PostResponse searchPost(String loginId, String recipeTitle, String postTitle , Long lastPostId, Pageable pageable);

    void deleteComments(List<Long> ids);

    void deletePosts(List<Long> postIds);
}
