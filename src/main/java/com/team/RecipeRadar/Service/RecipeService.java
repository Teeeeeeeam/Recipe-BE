package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Recipe;
import com.team.RecipeRadar.dto.AddRecipeRequest;
import com.team.RecipeRadar.dto.UpdateRecipeRequest;
import com.team.RecipeRadar.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public Recipe save(AddRecipeRequest request) {
        return recipeRepository.save(request.toEntity());
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional
    public Recipe update(long id, UpdateRecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        recipe.update(request.getRecipeTitle(), request.getRecipeContent(), request.getRecipeServing(), request.getCookingTime(), request.getIngredientsAmount(), request.getCookingStep(), request.getRecipeLevel());

        return recipe;
    }
    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.findByRecipeTitleContainingIgnoreCase(query);
    }
}
