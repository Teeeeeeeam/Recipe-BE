package com.team.RecipeRadar.domain.Image.application;

import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements ImageService {

    private final ImgRepository imgRepository;
    private final S3UploadService s3UploadService;


    @Override
    public void saveRecipeImg(Recipe recipe, UploadFile uploadFile) {
        uploadFile.setRecipe(recipe);
        imgRepository.save(uploadFile);
    }

    @Override
    public void delete_Recipe(Long recipeId) {
        log.info("시작={}", LocalDateTime.now());
        List<String> allStoredName = imgRepository.findAllStoredName(recipeId);

        allStoredName.stream().forEach(s -> s3UploadService.deleteFile(s));

        imgRepository.delete_recipe_img(recipeId);
        log.info("끝={}",LocalDateTime.now());
    }
}
