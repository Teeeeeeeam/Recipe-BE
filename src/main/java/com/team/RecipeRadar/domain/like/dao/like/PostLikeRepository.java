package com.team.RecipeRadar.domain.like.dao.like;

import com.team.RecipeRadar.domain.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long>, PostLikeRepositoryCustom{

    Boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    void deleteByMemberIdAndPostId(Long memberId, Long postId);
    @Modifying
    @Query("DELETE from PostLike p where p.post.id =:postId")
    void deletePostID(@Param("postId")Long postId);
}
