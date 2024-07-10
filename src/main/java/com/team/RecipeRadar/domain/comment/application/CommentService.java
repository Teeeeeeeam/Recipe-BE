package com.team.RecipeRadar.domain.comment.application;


import com.team.RecipeRadar.domain.blackList.dto.response.PostsCommentResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    void save(Long postId, String comment,Long memberId);
    void deleteComment(Long commentId, Long memberId);
    void update(Long commentId,String newComment ,Long memberId);
    PostsCommentResponse getPostsComments(Long postId, Long lastId, Pageable pageable);
}
