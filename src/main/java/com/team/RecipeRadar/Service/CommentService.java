package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.dto.AddCommentRequest;
import com.team.RecipeRadar.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    Comment save(AddCommentRequest request);

    List<Comment> findAll();

    Comment findById(long id);

    void delete(long id);

    Comment update(long id, UpdateCommentRequest request);

    List<Comment> searchComments(String query);
}
