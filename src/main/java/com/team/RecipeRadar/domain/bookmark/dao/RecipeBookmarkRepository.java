package com.team.RecipeRadar.domain.bookmark.dao;

import com.team.RecipeRadar.domain.bookmark.domain.RecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeBookmarkRepository extends JpaRepository<RecipeBookmark,Long> ,CustomRecipeBookmarkRepository{

    boolean existsByMember_IdAndRecipe_Id(Long memberId, Long recipeId);
    void deleteByMember_IdAndRecipe_Id(Long memberId, Long recipeId);
    void deleteAllByRecipe_Id(Long recipeId);
}
