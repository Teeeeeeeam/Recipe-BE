package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeSearchedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<RecipeSearchedDto>  searchRecipe(List<String> ingredientTitles, Pageable pageable) {

       Page<Recipe> recipePage = recipeRepository.findRecipeByIngredient(ingredientTitles, pageable);

        List<RecipeSearchedDto> recipeDtos = recipePage.getContent().stream()
                .map(RecipeSearchedDto::of)
                .collect(Collectors.toList());
       return recipeDtos;
    }

}

