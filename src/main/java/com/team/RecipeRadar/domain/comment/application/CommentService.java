package com.team.RecipeRadar.domain.comment.application;


import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserAddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.team.RecipeRadar.domain.comment.domain.Comment;

public interface CommentService {
    Comment save(UserAddCommentRequest userAddCommentDto);


    void delete_comment(UserDeleteCommentRequest userDeleteCommentDto);

    Page<CommentDto> commentPage(Long id, Pageable pageable);

    void update(Long member_id,Long comment_id,String Content);

    Comment findById(long id);
}
