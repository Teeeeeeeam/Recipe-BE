package com.team.RecipeRadar.domain.comment.application;


import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserAddCommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.AddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {


    Comment save(UserAddCommentDto userAddCommentDto);
    void delete_comment(UserDeleteCommentDto userDeleteCommentDto);

    Page<CommentDto> commentPage(Long id, Pageable pageable);

    void update(Long member_id,Long comment_id,String Content);

}
