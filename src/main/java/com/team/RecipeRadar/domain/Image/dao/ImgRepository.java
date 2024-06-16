package com.team.RecipeRadar.domain.Image.dao;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImgRepository extends JpaRepository<UploadFile,Long> ,CustomImgRepository{

    @Query("select u from UploadFile u where u.recipe.id=:recipeId AND u.post.id is null")
    Optional<UploadFile> findrecipeIdpostNull(@Param("recipeId") Long recipe_id);

    UploadFile findByQuestionId(Long questionId);
    @Modifying
    @Query("delete from UploadFile u where u.post.id=:postId and u.recipe.id=:recipeId")
    void deletePostImg(@Param("postId")Long postId,@Param("recipeId")Long recipeId);

    @Query("select u from UploadFile u where u.post.id=:postId")
    UploadFile getOriginalFileName(@Param("postId")Long postId);

    UploadFile findByNoticeId(Long noticeId);

    UploadFile findByPostId(Long postId);
    @Modifying
    @Query("delete from UploadFile u where u.notice.id =:noticeId")
    void deleteNoticeId(@Param("noticeId")Long noticeId);

}
