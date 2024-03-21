package com.team.RecipeRadar.domain.comment.application;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.AddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    Comment save(AddCommentRequest request);

    List<Comment> findAll();

    Comment findById(long id);

    void delete(long id);

    Comment update(long id, UpdateCommentRequest request);

    List<Comment> searchComments(String query);
}
