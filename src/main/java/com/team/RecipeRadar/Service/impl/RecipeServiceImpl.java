package com.team.RecipeRadar.service.impl;

import com.team.RecipeRadar.Entity.Recipe;
import com.team.RecipeRadar.dto.AddRecipeRequest;
import com.team.RecipeRadar.dto.UpdateRecipeRequest;
import com.team.RecipeRadar.repository.RecipeRepository;
import com.team.RecipeRadar.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Override
    public Recipe save(AddRecipeRequest request) {
        return recipeRepository.save(request.toEntity());
    }

    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe findById(long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Override
    public void delete(long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Recipe update(long id, UpdateRecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        recipe.update(request.getRecipeTitle(), request.getRecipeContent(), request.getRecipeServing(), request.getCookingTime(), request.getIngredientsAmount(), request.getCookingStep(), request.getRecipeLevel());

        return recipe;
    }

    @Override
    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.findByRecipeTitleContainingIgnoreCase(query);
    }

    @Override
    public long getRecipeCount() {
        return recipeRepository.countByRecipeTitleIsNotNull();
    }
}