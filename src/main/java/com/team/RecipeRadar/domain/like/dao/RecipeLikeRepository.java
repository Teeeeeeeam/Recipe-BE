package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLike,Long> {

    boolean existsByMemberIdAndRecipeId(Long memberId,Long RecipeId);

    void deleteByMemberIdAndRecipeId(Long memberId,Long RecipeId);

}
