package com.team.RecipeRadar.domain.comment.api;

import com.team.RecipeRadar.domain.comment.application.CommentService;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserAddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserUpdateCommentRequest;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Tag(name = "사용자 - 댓글 컨트롤러",description = "사용자 댓글 관리")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "로그인한 사용자만 댓글을 작성할 수 있는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 작성 성공\", \"data\" : {\"commentContent\": \"[작성한 댓글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"created_at\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"회원정보나 게시글을 찾을수 없습니다.\"}]"))),
    })
    @PostMapping("/api/user/comments")
    public ResponseEntity<?> comment_add(@Valid @RequestBody UserAddCommentRequest userAddCommentRequest, BindingResult bindingResult){
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }
            Comment save = commentService.save(userAddCommentRequest);
            UserAddCommentRequest addResponse = new UserAddCommentRequest(save.getCommentContent(), save.getMember().getId(),save.getPost().getId(),save.getCreated_at());

            return ResponseEntity.ok(new ControllerApiResponse(true,"성공",addResponse));
    }

    @Operation(summary = "댓글 삭제",description = "로그인한 사용자만 댓글을 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"댓글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"해당 댓글 찾을 수없습니다. 1\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))
    })
    @DeleteMapping("/api/user/comments")
    public ResponseEntity<?> comment_delete(@RequestBody UserDeleteCommentRequest userDeleteCommentRequest){
           commentService.delete_comment(userDeleteCommentRequest);//반환타입 void
            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 삭제 성공"));
    }

    @Operation(summary = "댓글 모두 조회",description = "해당 게시글의 모든 댓글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
            content = @Content(schema = @Schema(implementation = CommentDto.class),
                    examples = @ExampleObject(value =  "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"content\":\"댓글 리스트\"}, \"pageable\":\"페이징 내용\"}")
            ))
    })
    @GetMapping("/api/comments")
    public ResponseEntity<?> comment_Page(@Parameter(description = "게시글 Id")@RequestParam(value = "postId",required = false)Long postId,
                                          Pageable pageable){
            Page<CommentDto> comments = commentService.commentPage(postId, pageable);
            return ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공", comments));
    }

    @Operation(summary = "댓글 수정 API",description = "로그인, 작성자만 수정가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 수정 성공\" , \"data\" : {\"commentContent\": \"[수정한 댓글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"update_At\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"수정할 댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"[오류내용]\"}]"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))
    })
    @PutMapping("/api/user/comments")
    public ResponseEntity<?> comment_update(@Valid @RequestBody UserUpdateCommentRequest userUpdateCommentRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
        }
        commentService.update(userUpdateCommentRequest.getMemberId(),userUpdateCommentRequest.getCommentId(),userUpdateCommentRequest.getCommentContent());
        Comment comment = commentService.findById(userUpdateCommentRequest.getCommentId());
        UserUpdateCommentRequest userUpdateCommentDto = new UserUpdateCommentRequest(comment.getCommentContent(), comment.getMember().getId(), comment.getId(), comment.getUpdated_at());

        return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 수정 성공",userUpdateCommentDto));
    }
}
