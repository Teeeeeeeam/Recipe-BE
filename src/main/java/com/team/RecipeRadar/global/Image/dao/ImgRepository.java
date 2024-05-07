package com.team.RecipeRadar.global.Image.dao;

import com.team.RecipeRadar.global.Image.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImgRepository extends JpaRepository<UploadFile,Long> {

    Optional<UploadFile> findByRecipe_Id(Long recipe_id);
}
