package com.team.RecipeRadar.domain.Image.dao;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImgRepository extends JpaRepository<UploadFile,Long> ,CustomImgRepository{

    @Query("select u from UploadFile u where u.recipe.id=:recipeId AND u.post.id is null")
    Optional<UploadFile> findUploadFileByRecipeIdAndPostNull(@Param("recipeId") Long recipeId);

    UploadFile findByQuestionId(Long questionId);
    @Modifying
    @Query("delete from UploadFile u where u.post.id=:postId and u.recipe.id=:recipeId")
    void deleteUploadFileByPostIdAndRecipeId(@Param("postId")Long postId, @Param("recipeId")Long recipeId);

    UploadFile findByNoticeId(Long noticeId);

    UploadFile findByPostId(Long postId);
    @Modifying
    @Query("delete from UploadFile u where u.notice.id =:noticeId")
    void deleteByNoticeId(@Param("noticeId")Long noticeId);

}
