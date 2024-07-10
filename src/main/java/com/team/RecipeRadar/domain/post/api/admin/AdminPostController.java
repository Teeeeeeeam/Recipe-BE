package com.team.RecipeRadar.domain.post.api.admin;

import com.team.RecipeRadar.domain.post.application.admin.AdminPostService;
import com.team.RecipeRadar.domain.blackList.dto.response.PostsCommentResponse;
import com.team.RecipeRadar.domain.post.dto.response.PostResponse;
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
