package com.team.RecipeRadar.domain.recipe.api.user;

import com.team.RecipeRadar.domain.recipe.application.user.RecipeService;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.response.*;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Tag(name = "사용자 - 레시피 컨트롤러", description = "레시피 검색 및 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 검색(무한 스크롤)", description = "조회된 마지막 레시피의 ID 값을 통해 다음 페이지 여부를 판단합니다. 'lastId'는 조회된 마지막 페이지의 작성된 값을 넣지 않으면 첫 번째 데이터만 출력됩니다." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":[{\"id\":128671,\"imageUrl\":\"링크.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"nextPage\":true}}")))
    })
    @GetMapping("/recipe")
    public ResponseEntity<?> findRecipe(@RequestParam(value = "ingredients",required = false) List<String> ingredients,
                                        @RequestParam(value = "lastId",required = false)Long lastRecipeId,
                                        @Parameter(example = "{\"size\":10}") Pageable pageable){
        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingredients,lastRecipeId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeResponse));
    }

    @Operation(summary = "레시피 검색 (기본 페이징)", description = "기본적인 페이지네이션 방식을 사용합니다. 기본적으로 레시피를 오름차순으로 정렬합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipes\":[{\"id\":128671,\"imageUrl\":\"https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/1fb83f8578488ba482ad400e3b62df49.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"totalPage\":1,\"totalElements\": 9}}")))
    })
    @GetMapping("/recipe/normal")
    public ResponseEntity<?> findRecipeV1(@RequestParam(value = "ingredients",required = false) List<String> ingredients,
                                          @RequestParam(value = "title", required = false) String title,
                                          @Parameter(example = "{\"page\":2,\"size\":10}") Pageable pageable){
        RecipeNormalPageResponse recipeNormalPageResponse = recipeService.searchRecipeByIngredientsNormal(ingredients, title, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeNormalPageResponse));
    }

    @Operation(summary = "레시피 상제 정보",description = "해당 레시피의 자세한 정보를 보기위한 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":{\"id\":128671,\"imageUrl\":\"링크.jpg\"," +
                                    "\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0,\"createdAt\":\"2024-06-25\",\"cookIngredients\":\"PROCESSED_FOOD\",\"cookMethods\":\"DEEP_FRYING\",\"dishTypes\":\"DESSERT\"},\"ingredients\":[\"어묵 2개\",\"재료 데이터\"]," +
                                    "\"cookSteps\":[{\"cookStepId\":\"193\",\"cookSteps\":\"당근과 양파는 깨끗히 씻으신 후에 채썰어 준비한 후 후라이팬에 기름을 두르고 팬을 달군 후 당근| 양파를 살짝 볶아주세요.\"}]}}")))
    })
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> recipeDetails(@Schema(example = "221094")@PathVariable("recipeId")Long recipeId){
        RecipeDetailsResponse recipeDetails = recipeService.getRecipeDetails(recipeId);
        return  ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공",recipeDetails));
    }
    @Operation(summary = "레시피 좋아요순 조회",description = "좋아요가 많은 레시피 중 상위 8개를 출력하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipe\":[\"8개의 레시피 데이터\"]}}")))
    })
    @GetMapping("/main/recipe")
    public ResponseEntity<?> mainRecipe(){
        MainPageRecipeResponse mainPageRecipeResponse = recipeService.mainPageRecipe();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",mainPageRecipeResponse));
    }

    @Operation(summary = "레시피 카테고리 검색",description = "레시피의 카테고리를 통해서 해당 레시피를 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"recipes\":[{\"id\":\"7014622\",\"imageUrl\":\"https://recipe1.ezmember.co.kr/cache/recipe/2023/11/29/40bdfa564314d9024353b55d84ed79241.jpg\",\"title\":\"양갈비\", \"cookingLevel\": \"아무나\",\"people\": \"2인분\",\"likeCount\":0}]}}")))
    })
    @GetMapping("/category/recipe")
    public ResponseEntity admin(@RequestParam(value = "cat1",required = false) List<CookIngredients> ingredients,
                                @RequestParam(value = "cat2",required = false) List<CookMethods> cookMethods,
                                @RequestParam(value = "cat3",required = false) List<DishTypes> dishTypes,
                                @RequestParam(value = "lastCount",required = false)  Integer likeCount,
                                @RequestParam(value = "order",defaultValue = "DATE") OrderType order,
                                @RequestParam(value = "lastId",required = false) Long lastId,
                                @Parameter(example = "{\"size\":10}") Pageable pageable){
        RecipeCategoryResponse recipeCategoryResponse = recipeService.searchCategory(ingredients, cookMethods, dishTypes, order, likeCount, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeCategoryResponse));
    }
}
