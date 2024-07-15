package com.team.RecipeRadar.domain.comment.dao;


import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomCommentRepository {

    Slice<CommentDto> getCommentsByPostId(Long postId, Long lastId, Pageable pageable);
    void deleteCommentsByRecipeId(Long recipeId);
}
