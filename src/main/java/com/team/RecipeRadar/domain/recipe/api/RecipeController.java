package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.recipe.application.RecipeService;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.AddRecipeRequest;
import com.team.RecipeRadar.domain.recipe.dto.RecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.UpdateRecipeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/api/admin/recipes")
    public ResponseEntity<Recipe> addRecipe(@RequestBody AddRecipeRequest request) {
        Recipe savedRecipe = recipeService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedRecipe);
    }

    @GetMapping("/api/admin/recipes")
    public  ResponseEntity<List<RecipeResponse>> findAllRecipes() {
        List<RecipeResponse> recipes = recipeService.findAll()
                .stream()
                .map(RecipeResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(recipes);
    }
    @GetMapping("api/admin/recipes/{id}")
    public  ResponseEntity<RecipeResponse> findRecipe(@PathVariable long id) {
        Recipe recipe = recipeService.findById(id);

        return  ResponseEntity.ok()
                .body(new RecipeResponse(recipe));
    }

    @DeleteMapping("/api/admin/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        recipeService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/admin/recipes/{id}")
    public  ResponseEntity<Recipe> updateRecipe(@PathVariable long id, @RequestBody UpdateRecipeRequest request){
        Recipe updateRecipe = recipeService.update(id, request);

        return ResponseEntity.ok()
                .body(updateRecipe);
    }

    @GetMapping("/api/recipes/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipe(@RequestParam String query) {
        List<Recipe> recipes = recipeService.searchRecipes(query);
        List<RecipeResponse> recipeResponses = recipes.stream()
                .map(RecipeResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(recipeResponses);
    }
}
