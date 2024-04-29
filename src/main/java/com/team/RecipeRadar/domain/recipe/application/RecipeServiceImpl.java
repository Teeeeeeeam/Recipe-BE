package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Ingredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.DetailRecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.SearchRecipeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    private String filePath = "src/main/resources/data/TB_RECIPE.csv";


    @Override
    public void saveRecipeData() {
        List<String[]> dataList = new ArrayList<>();
        String csvSplitBy = "\t"; // CSV 파일에서 필드를 구분하는 문자 (탭)

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            // 첫 줄은 헤더이므로 스킵
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                // CSV 파일의 각 라인을 탭으로 분할하여 배열로 저장
                String[] data = line.split(csvSplitBy);

                String[] dataArray = data[0].split(",");
                //[0] : 레시피 번호 | [2] : 레시피 제목 | [13] : 레시피 재료 | [14] : 인원수 | [15] : 난이도 | [16] : 조리 시간
                String[] result = new String[]{dataArray[0],dataArray[2], dataArray[13], dataArray[14],dataArray[15],dataArray[16]};
                log.info("aa={}", Arrays.stream(result).toList());

                dataList.add(result);
            }

            saveFile(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveFile(List<String[]> list){
        LocalDateTime start = LocalDateTime.now();

        for (String[] ingredients : list) {
            String inger="[재료]";
            String replace_inger = ingredients[2].replace(inger, "");
            Pattern pattern = Pattern.compile("\\[.*?\\]");
            Matcher matcher = pattern.matcher(replace_inger);
            String remove_type1 = matcher.replaceAll("|");

            String replace = remove_type1.replace("|", ",");
            if (!replace.isEmpty() && replace.charAt(0) == ',') {
                replace = replace.substring(1); // 첫 번째 문자(쉼표) 제거
            }
            System.out.println("최종 변경된 값= "+replace);

            //[0] : 레시피 번호 | [1] : 레시피 제목 | [2] : 레시피 재료 | [3] : 인원수 | [4] : 난이도 | [5] : 조리 시간
            Recipe recipe = Recipe.builder()
                    .id(ingredients[0])
                    .title(ingredients[1])
                    .people(ingredients[3])
                    .cookingLevel(ingredients[4])
                    .cookingTime(ingredients[5])
                    .likeCount(0)
                    .build();


            Recipe save = recipeRepository.save(recipe);

            Ingredient ingredient = Ingredient.builder()
                    .ingredients(replace)
                    .recipe(save).build();

            ingredientRepository.save(ingredient);

        }
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);

        System.out.println("총시간= "+duration);
    }

    @Override
    public Page<SearchRecipeResponse> searchRecipe(List<String> ingredients, Pageable pageable) {
        return null;
    }

    @Override
    public DetailRecipeResponse detailRecipeInfo(String recipeId) {
        return null;
    }
}
