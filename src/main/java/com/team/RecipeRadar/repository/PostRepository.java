package com.team.RecipeRadar.repository;

import com.team.RecipeRadar.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
