package com.team.RecipeRadar.global.Crawling;

import com.team.RecipeRadar.domain.recipe.dao.cookingSetp.CookStepRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingService {

    private final RecipeRepository recipeRepository;
    private final CookStepRepository cookStepRepository;
    private final IngredientRepository ingredientRepository;
    private final CrawlingUtils crawlingUtils;

    public void save() {
        List<String> collect = recipeRepository.findAll().stream()
                .map(Recipe::getId)
                .collect(Collectors.toList());


        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount*7);

        for (String recipe_id : collect) {
            executor.execute(() -> {
                CrawlingRequest process = crawlingUtils.process(recipe_id);
                List<String> steps1 = process.getSteps();

                Recipe recipe = recipeRepository.findById(recipe_id).orElseThrow();
                recipe.setImageUrl(process.getImgUrl());
                Recipe save = recipeRepository.save(recipe);
                log.info("recipe saved: {}", save);

                for (String step : steps1) {
                    StringTokenizer st = new StringTokenizer(step, "|");
                    List<CookingStep> steps = new ArrayList<>();

                    while (st.hasMoreTokens()) {
                        String s = st.nextToken();
                        CookingStep build = CookingStep.builder().steps(s).recipe(recipe).build();
                        steps.add(build);
                        log.info("cooking step saved: {}", steps);
                    }
                    cookStepRepository.saveAll(steps);
                }
            });
        }
        executor.shutdown();
    }

    public void update_ingredient(){
        List<String> noIngredients = recipeRepository.getNoIngredients();
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount*7);

        for (String recipeId : noIngredients){
            log.info("id={}",recipeId);
            executor.execute(() -> {
                String ingredients = crawlingUtils.noIngredients(recipeId);
                Ingredient byRecipeId = ingredientRepository.findByRecipe_Id(recipeId);
                byRecipeId.setIngredients(ingredients);
                ingredientRepository.save(byRecipeId);
            });

        }
        executor.shutdown();
    }

    public void update_ingredient_v2(){
        List<String> noIngredients = recipeRepository.getNoIngredients();
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount*7);

        for (String recipeId : noIngredients){
            log.info("id={}",recipeId);
            executor.execute(() -> {
                String ingredients = crawlingUtils.noIngredients_v2(recipeId);
                Ingredient byRecipeId = ingredientRepository.findByRecipe_Id(recipeId);
                byRecipeId.setIngredients(ingredients);
                ingredientRepository.save(byRecipeId);
            });

        }
        executor.shutdown();
    }

    public void update_ingredient_v3(){
        List<String> noIngredients = recipeRepository.getNoIngredients();
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(coreCount*2);

        for (String recipeId : noIngredients){
            log.info("id={}",recipeId);
            executor.execute(() -> {
                String ingredients = crawlingUtils.noIngredients_v3(recipeId);
                Ingredient byRecipeId = ingredientRepository.findByRecipe_Id(recipeId);
                byRecipeId.setIngredients(ingredients);
                ingredientRepository.save(byRecipeId);
            });

        }
        executor.shutdown();
    }
}
