package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.recipe.application.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final RecipeService recipeService;

}
