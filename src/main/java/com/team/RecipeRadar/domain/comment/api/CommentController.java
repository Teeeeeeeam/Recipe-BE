package com.team.RecipeRadar.domain.comment.api;

import com.team.RecipeRadar.domain.comment.application.CommentService;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserAddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserUpdateCommentRequest;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

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
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"회원정보나 게시글을 찾을수 없습니다.\"}]"))),
    })
    @PostMapping("/api/user/comments")
    public ResponseEntity<?> comment_add(@Valid @RequestBody UserAddCommentRequest userAddCommentRequest, BindingResult bindingResult,
                                         @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
            if (bindingResult.hasErrors())
                return getErrorResponseResponse(bindingResult);

        MemberDto memberDto = getMemberDto(principalDetails);

        commentService.save(userAddCommentRequest.getPostId(),userAddCommentRequest.getCommentContent(),memberDto.getId());

            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 작성 성공"));
    }

    @Operation(summary = "댓글 삭제",description = "로그인한 사용자만 댓글을 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"댓글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"해당 댓글을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))
    })
    @DeleteMapping("/api/user/comments")
    public ResponseEntity<?> comment_delete(@RequestBody UserDeleteCommentRequest userDeleteCommentRequest,
                                            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = getMemberDto(principalDetails);
        commentService.deleteComment(userDeleteCommentRequest.getCommentId(),memberDto.getId());
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
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 수정 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"수정할 댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"[오류내용]\"}]"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))
    })
    @PutMapping("/api/user/comments")
    public ResponseEntity<?> comment_update(@Valid @RequestBody UserUpdateCommentRequest userUpdateCommentRequest, BindingResult bindingResult,
                                            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        if (bindingResult.hasErrors())
            return getErrorResponseResponse(bindingResult);

        MemberDto memberDto = getMemberDto(principalDetails);
        commentService.update(userUpdateCommentRequest.getCommentId(), userUpdateCommentRequest.getCommentContent(), memberDto.getId());

        return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 수정 성공"));
    }

    private static ResponseEntity<ErrorResponse<Map<String, String>>> getErrorResponseResponse(BindingResult bindingResult) {
        Map<String, String> result = new LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            result.put(error.getField(),error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "실패", result));
    }

    private static MemberDto getMemberDto(PrincipalDetails principalDetails) {
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        return memberDto;
    }
}
