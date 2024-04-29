package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLike,Long>, RecipeLikeRepositoryCustom {

    boolean existsByMemberIdAndRecipeId(Long memberId,String RecipeId);

    void deleteByMemberIdAndRecipeId(Long memberId,String RecipeId);

}
