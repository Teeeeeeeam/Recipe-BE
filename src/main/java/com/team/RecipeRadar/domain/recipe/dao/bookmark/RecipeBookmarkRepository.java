package com.team.RecipeRadar.domain.recipe.dao.bookmark;

import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeBookmarkRepository extends JpaRepository<RecipeBookmark,Long> {

    boolean existsByMember_IdAndRecipe_Id(Long memberId, Long recipeId);

    void deleteByMember_Id(Long memberId);
    void deleteByMember_IdAndRecipe_Id(Long memberId, Long recipeId);
}
