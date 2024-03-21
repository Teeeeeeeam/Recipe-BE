package com.team.RecipeRadar.domain.comment.dao;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCommentContentContainingIgnoreCase(String commentTitle);
}
