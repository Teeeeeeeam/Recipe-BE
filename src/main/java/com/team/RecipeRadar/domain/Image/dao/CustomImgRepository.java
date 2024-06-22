package com.team.RecipeRadar.domain.Image.dao;

import java.util.List;

public interface CustomImgRepository {

    void delete_recipe_img(Long recipeId);

    List<String> findAllStoredName(Long recipeId);

}
