package com.team.RecipeRadar.domain.comment.dao;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository{

    List<Comment> findAllByPostId(Long postId);
    @Modifying
    @Query("delete from Comment c where c.member.id=:memberId and c.id=:commentId")
    void deleteByMemberIdAndCommentId(@Param("memberId") Long member_id, @Param("commentId")Long comment_id);
    @Modifying
    @Query("delete from Comment c where c.post.id=:postId")
    void deleteByPostId(@Param("postId")Long postId);
    Page<Comment> findAllByPostId(Long post_id, Pageable pageable);
}