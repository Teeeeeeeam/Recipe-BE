package com.team.RecipeRadar.domain.like.api;

import com.team.RecipeRadar.domain.like.dto.response.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.application.LikeService;
import com.team.RecipeRadar.domain.like.dto.request.RecipeLikeRequest;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.payload.ErrorResponse;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class RecipeLikeController {

    @Qualifier("RecipeLikeServiceImpl")
    private final LikeService recipeLikeService;
    private final CookieUtils cookieUtils;


    @Tag(name = "사용자 - 좋아요/즐겨찾기 컨트롤러", description = "좋아요/즐겨찾기 확인 및 처리")
    @Operation(summary = "레시피 - 좋아요",
            description = "로그인한 사용자만 좋아요를 할 수 있으며, 기본값으로는 좋아여가 되어 있지 않다. 최초 요청시 좋아요가 되며 좋아요가된 상태에서 다시 요청을 하면 좋아요를 해제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "[{\"success\" : true, \"message\" : \"좋아요 성공\"}, {\"success\" : false, \"message\" : \"좋아요 해제\"}]"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @PostMapping("/recipe/like")
    public ResponseEntity<?> addLike(@RequestBody RecipeLikeRequest recipeLikeRequest,
                                     @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        Boolean addLike = recipeLikeService.addLike(recipeLikeRequest,principalDetails.getMemberId());
        ControllerApiResponse response;

        if (!addLike){
            response = new ControllerApiResponse(true,"좋아요 성공");
        }else
            response = new ControllerApiResponse(false, "좋아요 해제");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "레시피 - 좋아요 확인 여부",
            description = "로그인한 사용자가 해당 레시피를 좋아요 했는지 확인합니다. recipe-id가 제공되지 않으면 false 값으로 응답됩니다.",tags = "사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그인한 사용자 요청시",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"좋아요 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @GetMapping("/recipe/{recipeId}/like/check")
    public ResponseEntity<?> likeCheck(@Parameter(description = "레시피Id") @PathVariable(required = false) Long recipeId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        Boolean checkLike = recipeLikeService.checkLike(principalDetails.getMemberId(), recipeId);
        return ResponseEntity.ok(new ControllerApiResponse(checkLike,"좋아요 상태"));

    }

    @Operation(summary = "레시피 좋아요 내역(페이징)",description = "사용자가 좋아요한 레시피에 대한 무한 페이징을 제공합니다.",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":\"boolean\",\"content\":[{\"id\":\"[레시피 id]\", \"content\" :\"[레시피 내용]\", \"title\":\"[레시피 제목]\"}]}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"해당 회원을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"접근할 수 없는 사용자입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}")))
    })
    @GetMapping("/info/recipe/likes")
    public ResponseEntity<?> getUserLike(@RequestParam(value = "lastId",required = false) Long recipeLike_lastId,
                                         @Parameter(hidden = true) @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                         @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){

       cookieUtils.validCookie(cookieLoginId, principalDetails.getName());

       UserInfoLikeResponse userLikesByPage = recipeLikeService.getUserLikesByPage(principalDetails.getMemberId(), recipeLike_lastId, pageable);
       return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",userLikesByPage));
    }
}
