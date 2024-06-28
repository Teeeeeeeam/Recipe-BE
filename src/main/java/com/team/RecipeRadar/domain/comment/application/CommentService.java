package com.team.RecipeRadar.domain.comment.application;


import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.team.RecipeRadar.domain.comment.domain.Comment;

public interface CommentService {
    void save(Long postId, String comment,Long memberId);
    void deleteComment(Long commentId, Long memberId);
    Page<CommentDto> commentPage(Long id, Pageable pageable);
    void update(Long commentId,String newComment ,Long memberId);

}
