package com.team.RecipeRadar.domain.recipe.dao.bookmark;

import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeBookmarkRepository extends JpaRepository<RecipeBookmark,Long> ,CustomRecipeBookmarkRepository{

    boolean existsByMember_IdAndRecipe_Id(Long memberId, Long recipeId);
    void deleteByMember_Id(Long memberId);
    void deleteByMember_IdAndRecipe_Id(Long memberId, Long recipeId);

    void deleteAllByRecipe_Id(Long recipeId);

    List<RecipeBookmark> findAllByRecipeId(Long recipeId);
}
