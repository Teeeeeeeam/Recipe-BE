package com.team.RecipeRadar.global.Image.dao;

import java.util.List;

public interface CustomImgRepository {

    void deleteMemberImg(Long memberId);

    void delete_recipe_img(Long recipeId);

    List<String> findAllStoredName(Long recipeId);

}
