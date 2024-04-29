package com.team.RecipeRadar.global.Crawling;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CrawlingUtils {
    private static final String URL = "https://www.10000recipe.com/recipe/";

    public CrawlingRequest process(String recipe_id) {
        try {
            // 해당 URL에서 HTML 가져오기
            Document doc = Jsoup.connect(URL+recipe_id).get();

            // 조리 순서 단계들을 가지고 있는 div를 선택
            Elements steps = doc.select("div.view_step_cont");

            Elements images = doc.select("div.centeredcrop img");

            List<String> ing_steps = new ArrayList<>();
            // 각 단계들의 데이터를 추출
            for (Element step : steps) {
                String description = step.selectFirst(".media-body").text();        //재료순서
                ing_steps.add(description+"|");
            }

            String imageUrl = images.attr("src");

            return  new CrawlingRequest(imageUrl, ing_steps);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public  String noIngredients(String recipeId) {
        try {
            Document doc = Jsoup.connect(URL + recipeId).get();

            // 주재료를 가지고 있는 ul 요소를 선택
            Element mainIngredientList = doc.selectFirst("ul:has(b:containsOwn([재료]))");
            // 양념 및 소스재료를 가지고 있는 ul 요소 선택
            Element seasoningList = doc.selectFirst("ul:has(b:containsOwn([양념]))");

            StringBuilder sb = new StringBuilder();
            // 주재료 목록을 출력합니다.
            if (mainIngredientList != null) {
                Elements mainIngredients = mainIngredientList.select("li");
                for (Element ingredient : mainIngredients) {
                    Element ingredientNameElement = ingredient.selectFirst(".ingre_list_name a");
                    if (ingredientNameElement != null) {
                        String ingredientName = ingredientNameElement.text();
                        String ingredientAmount = ingredient.selectFirst(".ingre_list_ea").text();
                        sb.append(ingredientName).append(" ").append(ingredientAmount).append(", ");
                    }
                }
            }

            // 양념 및 소스재료 목록을 출력
            if (seasoningList != null) {
                Elements seasoningIngredients = seasoningList.select("li");
                for (Element ingredient : seasoningIngredients) {
                    Element ingredientNameElement = ingredient.selectFirst(".ingre_list_name a");
                    if (ingredientNameElement != null) {
                        String ingredientName = ingredientNameElement.text();
                        String ingredientAmount = ingredient.selectFirst(".ingre_list_ea").text();
                        sb.append(ingredientName).append(" ").append(ingredientAmount).append(", ");
                    }
                }
            }

            if (!sb.isEmpty()){
                sb.delete(sb.length()-2,sb.length()-1);
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    //다른 버전2번째의 재료 데이터 추출
    public String noIngredients_v2(String recipeId) {

        try {
            Document doc = Jsoup.connect(URL + recipeId).get();
            Elements noIngredientsElements = doc.select(".cont_ingre dt:contains([주재료]) + dd");
            Elements select = doc.select(".cont_ingre dt:contains([양념 및 소스재료]) + dd");
            Pattern pattern = Pattern.compile("\\[.*?\\]");

            String ing = noIngredientsElements.text();
            Matcher matcher = pattern.matcher(ing);
            String ingredient = matcher.replaceAll("");

            String sauce = select.text();
            String sauce_value = pattern.matcher(sauce).replaceAll("");

            return (ingredient + "," + sauce_value);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // 다른 버전의 재료 html 데이터 추출
    public String noIngredients_v3(String recipeId) {

        try {
            Document doc = Jsoup.connect(URL + recipeId).get();
            Elements ingredientElements = doc.select(".ingre_list_name");

            List<String> ingredients = new ArrayList<>();
            for (Element ingredientElement : ingredientElements) {
                ingredients.add(ingredientElement.text());
            }

            StringBuilder sb = new StringBuilder();
            for (String ingredient_Value : ingredients) {
                sb.append(ingredient_Value+",");
            }

            if (!sb.isEmpty()){
                sb.delete(sb.length()-1,sb.length());
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
