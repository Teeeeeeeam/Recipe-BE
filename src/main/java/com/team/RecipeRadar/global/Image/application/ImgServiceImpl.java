package com.team.RecipeRadar.global.Image.application;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements ImageService {

    private final ImgRepository imgRepository;


    @Override
    public void saveRecipeImg(Recipe recipe, UploadFile uploadFile) {
        uploadFile.setRecipe(recipe);
        imgRepository.save(uploadFile);
    }
}
