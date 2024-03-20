package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.dto.AddCommentRequest;
import com.team.RecipeRadar.dto.UpdateCommentRequest;
import com.team.RecipeRadar.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Comment save(AddCommentRequest request);

    List<Comment> findAll();

    Comment findById(long id);

    void delete(long id);

    Comment update(long id, UpdateCommentRequest request);

    List<Comment> searchComments(String query);

    Comment save(CommentDto commentDto);
    void delete_comment(CommentDto commentDto);

    Page<CommentDto> commentPage(Long id, Pageable pageable);

    void update(Long member_id,Long comment_id,String Content);

}
