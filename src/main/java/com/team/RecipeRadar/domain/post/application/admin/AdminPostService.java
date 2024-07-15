package com.team.RecipeRadar.domain.post.application.admin;

import com.team.RecipeRadar.domain.blackList.dto.response.PostsCommentResponse;
import com.team.RecipeRadar.domain.post.dto.response.PostResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminPostService {

    long searchAllPosts();
    void deleteComments(List<Long> ids);
    void deletePosts(List<Long> postIds);
}
