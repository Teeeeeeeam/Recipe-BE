package com.team.RecipeRadar.domain.post.api.admin;

import com.team.RecipeRadar.domain.post.application.admin.AdminPostService;
import com.team.RecipeRadar.domain.balckLIst.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.post.dto.user.PostResponse;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "어드민 - 게시글 컨트롤러",description = "게시글 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminPostController {

    private final AdminPostService adminService;
    @Operation(summary = "게시글수 조회", description = "작성된 게시글의 수를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":\"10\"}"))),
    })
    @GetMapping("/posts/count")
    public ResponseEntity<?> getAllPostsCount(){
        long searchAllMembers = adminService.searchAllPosts();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }


    @Operation(summary = "게시글의 작성된 댓글 조회", description = "게시글의 작성된 댓글을 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value =  "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":false,\"comments\":[{\"id\":16,\"commentContent\":\"댓글 내용 1\",\"createdAt\":\"2024-05-23T17:37:53\",\"member\":{\"nickname\":\"User2\",\"loginId\":\"user1\",\"username\":\"실명\"}},{\"id\":17,\"commentContent\":\"댓글 내용 2\",\"createdAt\":\"2024-05-23T17:37:53\",\"member\":{\"nickname\":\"User2\",\"loginId\":\"user1\",\"username\":\"실명\"}}]}}"))),
    })
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getPostsContainsComments(@PathVariable("postId") Long postId,@RequestParam(value = "lastId",required = false)Long lastId,
                                                      @Parameter(example = "{\"size\":10}") Pageable pageable){
        PostsCommentResponse postsComments = adminService.getPostsComments(postId, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",postsComments));
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
        PostResponse postResponse = adminService.searchPost(loginId, recipeTitle, postTitle, lastPostId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"검색 성공",postResponse));
    }

    @Operation(summary = "게시글 댓글 삭제",description = "게시글의 댓글을 단일, 일괄 삭제하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"댓글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"댓글을 찾을수 없습니다.\"}"))),
    })
    @DeleteMapping("/posts/comments")
    public ResponseEntity<?> deleteComments(@RequestParam(value = "commentIds") List<Long> commentsIds){
        adminService.deleteComments(commentsIds);
        return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 삭제 성공"));
    }

    @Operation(summary = "게시글 삭제",description = "요리글을 단일, 일괄 삭제하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"게시글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"게시글을 찾을수 없습니다.\"}"))),
    })
    @DeleteMapping("/posts")
    public ResponseEntity<?> deletePost(@RequestParam(value = "postIds") List<Long> postIds){
        adminService.deletePosts(postIds);
        return ResponseEntity.ok(new ControllerApiResponse(true,"게시글 삭제 성공"));
    }
}
