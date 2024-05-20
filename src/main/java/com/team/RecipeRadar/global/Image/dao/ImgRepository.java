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

    Optional<UploadFile> findByRecipe_Id(Long recipe_id);
    @Modifying
    @Query("delete from UploadFile u where u.recipe.id=:recipeId")
    void deleteRecipeId(@Param("recipeId") Long recipe_Id);
}
