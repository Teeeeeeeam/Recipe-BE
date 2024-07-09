package com.team.RecipeRadar.domain.post.api.user;

import com.team.RecipeRadar.domain.post.application.user.PostService;
import com.team.RecipeRadar.domain.post.dto.request.UserAddRequest;
import com.team.RecipeRadar.domain.post.dto.request.ValidPostRequest;
import com.team.RecipeRadar.domain.post.dto.response.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.request.UserUpdateRequest;
import com.team.RecipeRadar.domain.post.dto.response.*;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Tag(name = "사용자 - 게시글 컨트롤러", description = "사용자 게시글과 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class PostController {

    private final PostService postService;
    private final CookieUtils cookieUtils;

    @Operation(summary = "게시글 작성", description = "로그인한 사용자만 게시글 작성 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples =  @ExampleObject(value = "{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}"))),
    })
    @PostMapping(value = "/user/posts",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postAdd(@Valid @RequestPart UserAddRequest userAddPostRequest, BindingResult bindingResult,
                                     @RequestPart MultipartFile file,
                                     @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails) {
        postService.save(userAddPostRequest,principalDetails.getMemberId(),file);
        return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));
    }

    @Operation(summary = "전체 게시글 조회(페이징)", description = "모든 사용자가 해당 게시글의 페이지를 볼 수 있다.(무한페이징)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ControllerApiResponse.class)),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"posts\":[{\"id\":23,\"postTitle\":\"Delicious Pasta\",\"createdAt\":\"2024-05-23\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}},{\"id\":24,\"postTitle\":\"Spicy Tacos\",\"createdAt\":\"2024-05-23\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}}]}}"))),
    })
    @GetMapping("/posts")
    public ResponseEntity<?> findAllPosts(@RequestParam(value = "lastId",required = false) Long postId,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable) {
        PostResponse postResponse = postService.postPage(postId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",postResponse));
    }

    @Operation(summary = "게시글 상세 조회", description = "사용자가 게시글의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회성공\",\"data\":{\"post\":{\"id\":3,\"postTitle\":\"냉장고~\",\"postContent\":\"이 파스타는 정말 간단하고 맛있어요!\",\"nickName\":\"김민우랍니다\",\"creatAt\":\"2024-05-20\",\"postServing\":\"3인분\",\"postCookingTime\":\"30분\",\"postCookingLevel\":\"중\",\"postLikeCount\":0,\"postImageUrl\":\"http://example.com/pasta.jpg\", \"member\":{\"nickname\" :\"Admin\"},\"recipe\":{\"id\":\"123\",\"title\":\"김치\"}},\"comments\":[{\"id\":1,\"commentContent\":\"댓글 작성!\",\"nickName\":\"닉네임\",\"createdAt\":\"2024-05-20T02:26:00\"}]}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"해당하는 게시물이 없습니다.\"}")))
    })
    @GetMapping("/user/posts/{postId}")
    public ResponseEntity<?> findPost(@PathVariable("postId") Long postId ){
        PostDetailResponse postDetailResponse = postService.postDetail(postId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회성공",postDetailResponse));
    }

    @Operation(summary = "게시글 삭제",description = "작성한 사용자만이 해당 레시피를 삭제할 수 있습니다. 삭제 시 해당 게시물과 관련된 모든 데이터가 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"요리글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"게시글을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 이용 가능합니다.\"}")))
    })
    @DeleteMapping("/user/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId,
                                        @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
            postService.delete(principalDetails.getMemberId(),postId);
            return ResponseEntity.ok(new ControllerApiResponse(true,"게시글 삭제 성공"));
    }

    @Operation(summary = "게시글 수정",  description = "로그인한 사용자만 수정이 가능하며, 작성자만 수정할 수 있습니다. 비밀번호 검증을 통해 사용자를 확인한 후 해당 API에 접근할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"요리글 수정 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"게시글을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}")))
    })
    @PostMapping(value = "/user/update/posts/{postId}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> updatePost(@Valid @RequestPart UserUpdateRequest userUpdateRequest, BindingResult bindingResult,
                                         @RequestPart(required = false) MultipartFile file, @PathVariable("postId") Long postId,
                                         @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        postService.update(postId,principalDetails.getMemberId(),userUpdateRequest,file);

        return ResponseEntity.ok(new ControllerApiResponse(true,"요리글 수정 성공"));
    }


    @Operation(summary = "게시글 비밀번호 검증",description = "게시글 삭제 및 수정 시, 해당 메소드를 통해 게시글 작성 시 입력한 비밀번호를 검증합니다. 검증에 성공한 경우에만 수정 및 삭제가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"비밀번호 인증 성공\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"비밀번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 이용 가능합니다.\"}")))
    })
    @PostMapping("/user/valid/posts")
    public ResponseEntity<?> validPost(@RequestBody ValidPostRequest validPostRequest,
                                       @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){

            postService.validPostPassword(principalDetails.getMemberId(), validPostRequest);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"비밀번호 인증 성공"));
    }


    @Operation(summary = "게시글 작성 내역(페이징)",description = "사용자가 작성한 게시글의 무한 페이징입니다.",tags = {"사용자 - 마이페이지 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":\"boolean\",\"content\":[{\"id\":\"[게시글 id]\", \"postTitle\" :\"[게시글 제목]\"}]}}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}"))),
    })
    @GetMapping("/user/info/posts")
    public ResponseEntity<?> postTitlePage(@RequestParam(value = "lastId",required = false) Long lastId,
                                           @Parameter(hidden = true) @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                           @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
                                           @Parameter(example = "{\"size\":10}") Pageable pageable){
        cookieUtils.validCookie(cookieLoginId,principalDetails.getName());
        UserInfoPostResponse userInfoPostResponse = postService.userPostPage(principalDetails.getMemberId(),lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",userInfoPostResponse));
    }

    @Operation(summary = "리시피 게시글 좋아요순 조회",description = "레시피의 좋아요가 많은 게시글 top 4개를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\": 5,\"post\":[{\"postTitle\":\"게시글 제목\",\"createdAt\":\"2024-06-25\",\"postLikeCount\":0,\"postImageUrl\":\"https://recipe-reader-kr/ex_image.png\",\"member\":{\"nickname\":\"일반사용자\"}}]}}")))
    })
    @GetMapping("/top/posts")
    public ResponseEntity<?> recipeTopPosts(@RequestParam("recipeId")Long recipeId){
        PostLikeTopResponse top4RecipesByLikes = postService.getTop4RecipesByLikes(recipeId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",top4RecipesByLikes));
    }

    @Operation(summary = "메인페이지 게시글 좋아요순 조회",description = "현재 사이트에서 작성된 게시글중 좋아요가 제일 많은 게시글 3개를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\": 5,\"post\":[{\"postTitle\":\"게시글 제목\",\"createdAt\":\"2024-06-25\",\"postLikeCount\":0,\"postImageUrl\":\"https://recipe-reader-kr/ex_image.png\",\"member\":{\"nickname\":\"일반사용자\"}}]}}")))
    })
    @GetMapping("/posts/main")
    public ResponseEntity<?> mainTopPosts(){
        PostLikeTopResponse top4RecipesByLikes = postService.getTopMainsByLikes();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",top4RecipesByLikes));
    }

    @Operation(summary = "레시피 게시글의 좋아요순 조회(페이징)",description = "현재 레시피의 작성된 게시글을 좋아요 순으로 조회(무한페이징), 마지막 게시글의 좋아요가 0일경우 lastId 사용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\": 5,\"post\":[{\"postTitle\":\"게시글 제목\",\"createdAt\":\"2024-06-25\",\"postLikeCount\":0,\"postImageUrl\":\"https://recipe-reader-kr/ex_image.png\",\"member\":{\"nickname\":\"일반사용자\"}}]}}")))
    })
    @GetMapping("/posts/{recipeId}")
    public ResponseEntity<?> getRecipe(@PathVariable("recipeId") Long recipeId,
                                       @RequestParam(value = "lastCount",required = false)Integer lastCount,
                                       @RequestParam(value = "lastId",required = false)Long lastId,
                                       @Parameter(example = "{\"size\":10}") Pageable pageable){
        PostResponse postResponse = postService.postByRecipeId(recipeId, lastCount, lastId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",postResponse));
    }

    @Operation(summary = "게시글 검색",description = "사용자의 로그인 아이디와 게시글 제목, 스크랩한 요리에 대해 검색할 수 있는 API  단일 조건의 검색이 가능하며, 조건 데이터가 추가될 때마다 AND 조건으로 데이터를 추립니다. (무한 페이징)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ControllerApiResponse.class)),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"posts\":[{\"id\":23,\"postTitle\":\"Delicious Pasta\",\"createdAt\":\"2024-05-23\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}},{\"id\":24,\"postTitle\":\"Spicy Tacos\",\"createAt\":\"2024-05-23\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}}]}}"))),
    })
    @GetMapping("/posts/search")
    public ResponseEntity<?> searchPost(@RequestParam(value = "loginId",required = false) String loginId,
                                        @RequestParam(value = "recipeTitle",required = false) String recipeTitle,
                                        @RequestParam(value = "postTitle",required = false) String postTitle,
                                        @RequestParam(value = "lastId",required = false) Long lastPostId,
                                        @Parameter(example = "{\"size\":10}") Pageable pageable){
        PostResponse postResponse = postService.searchPost(loginId, recipeTitle, postTitle, lastPostId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"검색 성공",postResponse));
    }
}