package com.team.RecipeRadar.domain.like.dao.like;

import com.team.RecipeRadar.domain.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long>, PostLikeRepositoryCustom{

    Boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    void deleteByMemberIdAndPostId(Long memberId, Long postId);
    void deleteByPostId(Long postId);
}
