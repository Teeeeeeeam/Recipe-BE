package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.recipe.application.RecipeService;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeSearchedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/recipe")
@RestController
public class RecipeController {


    private final RecipeService recipeService;


    @GetMapping("/search")
    public List<RecipeSearchedDto> searchRecipe(@RequestParam List<String> ingredientTitles, Pageable pageable) {

        return recipeService.searchRecipe(ingredientTitles, pageable);
    }
}
