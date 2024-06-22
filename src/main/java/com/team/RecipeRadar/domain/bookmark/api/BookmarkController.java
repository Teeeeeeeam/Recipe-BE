package com.team.RecipeRadar.domain.bookmark.api;

import com.team.RecipeRadar.domain.bookmark.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.bookmark.dto.reqeust.BookMarkRequest;
import com.team.RecipeRadar.domain.bookmark.dto.response.UserInfoBookmarkResponse;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class BookmarkController {

    private final RecipeBookmarkService recipeBookmarkService;
    private final CookieUtils cookieUtils;

    @Tag(name = "사용자 - 좋아요/즐겨찾기 컨트롤러", description = "좋아요/즐겨찾기 확인 및 처리")
    @Operation(summary = "레시피 - 즐겨찾기",description = "레시피를 즐겨찾기 하는 API 즐겨찾기를 추가하면 true를 반환하고, 즐겨찾기를 해제하면 false를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"즐겨찾기 상태\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"오류 내용\"}")))
    })
    @PostMapping("/recipe")
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

    @Operation(summary = "레시피 - 즐겨찾기 상태 조회",
            description = "로그인하지 않은 사용자는 기본적으로 false를 반환하며, 로그인한 사용자는 해당 레시피의 즐겨찾기 여부에 따라 상태를 반환합니다.",tags = "사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"즐겨찾기 상태\"}")))
    })
    @GetMapping("/recipe/{recipeId}/bookmarks/check")
    public ResponseEntity<?> bookmarksCheck(@Parameter(description = "레시피 아이디") @PathVariable(value = "recipeId",required = false) Long recipeId,
                                            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        Boolean checkBookmark = recipeBookmarkService.checkBookmark(principalDetails.getMemberId(), recipeId);

        return ResponseEntity.ok(new ControllerApiResponse(checkBookmark,"즐겨 찾기 상태"));
    }

    @Operation(summary = "즐겨찾기 내역(페이징)", description = "사용자가 즐겨찾기한 레시피에 대해 무한 페이징을 제공합니다.",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage \":true,\"bookmarkList\":[{\"id\":128671,\"title\":\"어묵김말이\"}]}}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}")))
    })
    @GetMapping("/info/bookmark")
    public ResponseEntity<?> userInfoBookmark(@RequestParam(value = "lastId",required = false)Long lastId,
                                              @Parameter(hidden = true)@CookieValue(name = "login-id",required = false) String cookieLoginId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @Parameter(example = "{\"size\":10}") Pageable pageable){
        validCookie(cookieLoginId,principalDetails);
        UserInfoBookmarkResponse userInfoBookmarkResponse = recipeBookmarkService.userInfoBookmark(principalDetails.getMemberId(), lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공", userInfoBookmarkResponse));
    }

    private void validCookie(String cookieLoginId, PrincipalDetails principalDetails) {
        cookieUtils.validCookie(cookieLoginId, principalDetails.getName());
    }
}
