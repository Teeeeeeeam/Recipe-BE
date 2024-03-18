package com.team.RecipeRadar.repository;

import com.team.RecipeRadar.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
