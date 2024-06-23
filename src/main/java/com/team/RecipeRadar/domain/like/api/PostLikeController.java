package com.team.RecipeRadar.domain.like.api;

import com.team.RecipeRadar.domain.like.dto.response.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.application.LikeService;
import com.team.RecipeRadar.domain.like.dto.request.PostLikeRequest;
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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class PostLikeController {

    @Qualifier("PostLikeServiceImpl")
    private final LikeService postLikeService;
    private final CookieUtils cookieUtils;

    @Tag(name = "사용자 - 좋아요/즐겨찾기 컨트롤러", description = "좋아요/즐겨찾기 확인 및 처리")
    @Operation(summary = "게시글 - 좋아요",
            description = "로그인한 사용자만 가능 최초 요청 시 좋아요를 추가하고, 다시 요청하면 좋아요가 해제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "[{\"success\" : true, \"message\" : \"좋아요 성공\"}, {\"success\" : false, \"message\" : \"좋아요 해제\"}]"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @PostMapping("/posts/like")
    public ResponseEntity<?> addLike(@RequestBody PostLikeRequest postLikeRequest,
                                     @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        Boolean addLike = postLikeService.addLike(postLikeRequest,principalDetails.getMemberId());

        ControllerApiResponse response;
        if (!addLike){
            response = new ControllerApiResponse(true,"좋아요 성공");
        }else
            response = new ControllerApiResponse(false, "좋아요 해제");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 - 좋아요 여부 확인",
            description = "로그인한 사용자가 해당 게시글을 좋아요 했는지 확인합니다. postId가 제공되지 않으면 false 값으로 응답됩니다.",tags = "사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그인한 사용자 요청시",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"좋아요 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @GetMapping("/posts/{postId}/like/check")
    public ResponseEntity<?> likeCheck(@Parameter(description = "게시글 Id") @PathVariable(required = false) Long postId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){

        Boolean checkLike = postLikeService.checkLike(principalDetails.getMemberId(), postId);

        return ResponseEntity.ok(new ControllerApiResponse(checkLike,"좋아요 상태"));

    }

    @Operation(summary = "게시글 좋아요 내역(페이징)",description = "사용자가 좋아요한 게시글의 무한 페이징입니다.",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":\"boolean\",\"content\":[{\"id\":\"[게시글 id]\", \"content\" :\"[게시글 내용]\", \"title\":\"[게시글 제목]\"}]}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"해당 회원을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401",description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"접근할 수 없는 사용자입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}")))
         })
    @GetMapping("/info/posts/likes")
    public ResponseEntity<?> getUserLike(@RequestParam(value = "lastId",required = false)Long postLike_lastId,
                                         @Parameter(hidden = true) @CookieValue(name = "login-id") String cookieLoginId,
                                         @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){

        cookieUtils.validCookie(cookieLoginId, principalDetails.getName());

        UserInfoLikeResponse userLikesByPage = postLikeService.getUserLikesByPage(principalDetails.getMemberId(), postLike_lastId, pageable);

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",userLikesByPage));

    }


}
