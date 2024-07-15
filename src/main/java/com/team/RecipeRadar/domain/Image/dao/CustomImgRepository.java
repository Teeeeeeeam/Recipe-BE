package com.team.RecipeRadar.domain.Image.dao;

import java.util.List;

public interface CustomImgRepository {

    void deleteImagesByRecipeId(Long recipeId);

    List<String> findAllStoredNamesByRecipeId(Long recipeId);

}
