package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.recipe.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.recipe.application.RecipeService;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Slf4j
public class RecipeController {

    private final RecipeBookmarkService recipeBookmarkService;
    private final RecipeService recipeService;

    @Tag(name = "사용자 - 레시피 컨트롤러", description = "레시피 검색 및 관리")
    @Operation(summary = "레시피 검색(무한 스크롤)", description = "조회된 마지막 레시피의 ID 값을 통해 다음 페이지 여부를 판단합니다. 'lastId'는 조회된 마지막 페이지의 작성된 값을 넣지 않으면 첫 번째 데이터만 출력됩니다." ,tags ="사용자 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":[{\"id\":128671,\"imageUrl\":\"링크.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"nextPage\":true}}")))
    })
    @GetMapping("/recipe")
    public ResponseEntity<?> findRecipe(@RequestParam("ingredients") List<String> ingredients,
                                        @RequestParam(value = "lastId",required = false)Long lastRecipeId,
                                        @Parameter(example = "{\"size\":10}") Pageable pageable){
        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingredients,lastRecipeId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeResponse));
    }

    @Operation(summary = "레시피 검색 (기본 페이징)", description = "기본적인 페이지네이션 방식을 사용합니다. 기본적으로 레시피를 오름차순으로 정렬합니다." ,tags ="사용자 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipes\":[{\"id\":128671,\"imageUrl\":\"https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/1fb83f8578488ba482ad400e3b62df49.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"totalPage\":1,\"totalElements\": 9}}")))
    })
    @GetMapping("/recipe/normal")
    public ResponseEntity<?> findRecipeV1(@RequestParam(value = "ingredients",required = false) List<String> ingredients,@RequestParam(value = "title", required = false) String title ,
                                          @Parameter(example = "{\"page\":2,\"size\":10}") Pageable pageable){
        RecipeNormalPageResponse recipeNormalPageResponse = recipeService.searchRecipeByIngredientsNormal(ingredients, title, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeNormalPageResponse));
    }

    @Operation(summary = "레시피 - 즐겨찾기",description = "레시피를 즐겨찾기 하는 API 즐겨찾기를 추가하면 true를 반환하고, 즐겨찾기를 해제하면 false를 반환합니다.",tags ="사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"즐겨찾기 상태\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"오류 내용\"}")))
    })
    @PostMapping("/user/recipe")
    public ResponseEntity<?> saveRecipeBookmark(@RequestBody BookMarkRequest bookMarkRequest,
                                                @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        Boolean bookmark = recipeBookmarkService.saveBookmark(principalDetails.getMemberId(), bookMarkRequest.getRecipeId());
        ControllerApiResponse response;
        if (!bookmark){
            response = new ControllerApiResponse(true,"북마크 성공");
        }else
            response = new ControllerApiResponse(false,"북마크 해제");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "레시피 상제 정보",description = "해당 레시피의 자세한 정보를 보기위한 API",tags ="사용자 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":{\"id\":128671,\"imageUrl\":\"링크.jpg\"," +
                                    "\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0},\"ingredients\":[\"어묵 2개\",\"재료 데이터\"]," +
                                    "\"cookStep\":[{\"cook_step_id\":\"193\",\"cook_steps\":\"당근과 양파는 깨끗히 씻으신 후에 채썰어 준비한 후 후라이팬에 기름을 두르고 팬을 달군 후 당근| 양파를 살짝 볶아주세요.\"}]}}")))
    })
    @GetMapping("/recipe/{id}")
    public ResponseEntity<?> getDetials(@Schema(example = "221094")@PathVariable("id")Long recipe_id){
        RecipeDetailsResponse recipeDetails = recipeService.getRecipeDetails(recipe_id);

        return  ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공",recipeDetails));
    }
    @Operation(summary = "레시피 좋아요순 조회",description = "좋아요가 많은 레시피 중 상위 8개를 출력하는 API",tags ="사용자 - 레시피 컨트롤러")
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

    @Operation(summary = "레시피 - 즐겨찾기 상태 조회",
            description = "로그인하지 않은 사용자는 기본적으로 false를 반환하며, 로그인한 사용자는 해당 레시피의 즐겨찾기 여부에 따라 상태를 반환합니다.", tags ="사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그인한 사용자 요청시",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"즐겨찾기 상태\"}")))
    })
    @GetMapping("/check/bookmarks")
    public ResponseEntity<?> bookmarksCheck(@Parameter(description = "레시피 아이디") @RequestParam(value = "recipe-id",required = false) Long recipeId){
        try {
            Boolean isBookmark;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication instanceof AnonymousAuthenticationToken){
                isBookmark = false;
            }else {
                PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
                MemberDto memberDto = principal.getMemberDto(principal.getMember());
                Long member_Id = memberDto.getId();
                isBookmark = recipeBookmarkService.checkBookmark(member_Id, recipeId);
            }
            return ResponseEntity.ok(new ControllerApiResponse(isBookmark,"즐겨 찾기 상태"));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
