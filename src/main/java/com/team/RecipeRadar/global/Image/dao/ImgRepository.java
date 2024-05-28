package com.team.RecipeRadar.global.Image.dao;

import com.team.RecipeRadar.global.Image.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImgRepository extends JpaRepository<UploadFile,Long> {

    @Query("select u from UploadFile u where u.recipe.id=:recipeId AND u.post.id is null")
    Optional<UploadFile> findByRecipe_Id(@Param("recipeId") Long recipe_id);
    @Modifying
    @Query("delete from UploadFile u where u.recipe.id=:recipeId")
    void deleteRecipeId(@Param("recipeId") Long recipe_Id);

    @Modifying
    @Query("delete from UploadFile u where u.post.id=:postId and u.recipe.id=:recipeId")
    void deletePostImg(@Param("postId")Long postId,@Param("recipeId")Long recipeId);

    @Query("select u from UploadFile u where u.post.id=:postId")
    UploadFile getOriginalFileName(@Param("postId")Long postId);

    @Modifying
    @Query("delete from UploadFile u where u.notice.id =:noticeId")
    void deleteNoticeId(@Param("noticeId")Long noticeId);

    @Query("select u from UploadFile u where u.notice.id=:noticeId")
    UploadFile getByNoticeOriginalFileName(@Param("noticeId")Long noticeId);
}
