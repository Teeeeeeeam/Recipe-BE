package com.team.RecipeRadar.repository;

import com.team.RecipeRadar.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCommentContentContainingIgnoreCase(String commentTitle);
}
