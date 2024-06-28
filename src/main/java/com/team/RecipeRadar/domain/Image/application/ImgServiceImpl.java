package com.team.RecipeRadar.domain.Image.application;

import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements ImageService {

    private final ImgRepository imgRepository;
    private final S3UploadService s3UploadService;


    /**
     * 레시피 이미지를 저장하는 메서드
     */
    @Override
    public void saveRecipeImage(Recipe recipe, UploadFile uploadFile) {
        uploadFile.setRecipe(recipe);
        imgRepository.save(uploadFile);
    }

    /**
     * 레시피 이미지를 삭제하는 메서드
     */
    @Override
    public void deleteRecipe(Long recipeId) {
        List<String> allStoredName = imgRepository.findAllStoredNamesByRecipeId(recipeId);
        allStoredName.stream().forEach(s -> s3UploadService.deleteFile(s));
        imgRepository.deleteImagesByRecipeId(recipeId);
    }
}
