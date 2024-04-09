package com.team.RecipeRadar.domain.comment.api;

import com.team.RecipeRadar.domain.comment.application.CommentService;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserAddCommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentDto;
import com.team.RecipeRadar.domain.comment.dto.user.UserUpdateCommentDto;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.CommentException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "어드민 댓글 컨트롤러", description = "어드민 관련 댓글 작업"),
        @Tag(name = "일반 사용자 댓글 컨트롤러", description = "일반 사용자 관련 댓글 작업")
})
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성 API", description = "로그인한 사용자만 댓글을 작성 가능", tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 작성 성공\", \"data\" : {\"commentContent\": \"[작성한 댓글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"created_at\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"회원정보나 게시글을 찾을수 없습니다.\"}]"))),
    })
    @PostMapping("/api/user/comments")
    public ResponseEntity<?> comment_add(@Valid @RequestBody UserAddCommentDto userAddCommentDto, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }
            Comment save = commentService.save(userAddCommentDto);

            UserAddCommentDto addResponse = new UserAddCommentDto(save.getCommentContent(), save.getMember().getId(),save.getPost().getId(),save.getCreated_at());
            
            return ResponseEntity.ok(new ControllerApiResponse(true,"성공",addResponse));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "댓글 삭제 API",description = "로그인한 사용자만 댓글 삭제가능",tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"댓글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

    })
    @DeleteMapping("/api/user/comments")
    public ResponseEntity<?> comment_delete(@RequestBody UserDeleteCommentDto userDeleteCommentDto){
        try{
           commentService.delete_comment(userDeleteCommentDto);//반환타입 void
            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }

    @Operation(summary = "댓글 모두 조회 API",description = "해당 게시글의 댓글을 모두 조회 (default size = 10, sort는 사용하지 않았음)",tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
            content = @Content(schema = @Schema(implementation = CommentDto.class),
                    examples = @ExampleObject(value =  "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"content\":\"댓글 리스트\"}, \"pageable\":\"페이징 내용\"}")
            )),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/comments")
    public ResponseEntity<?> comment_Page(@PageableDefault Pageable pageable,
                                          @Parameter(description = "게시글 Id")@RequestParam(value = "posts",required = false)String postid){
        try {
            Page<CommentDto> comments = commentService.commentPage(Long.parseLong(postid), pageable);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공", comments));
        }catch (CommentException e){
          throw new CommentException(e.getMessage());
        } catch (Exception e){
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Operation(summary = "댓글 수정 API",description = "로그인, 작성자만 수정가능",tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"댓글 수정 성공\" , \"data\" : {\"commentContent\": \"[수정한 댓글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"update_At\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",

                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"수정할 댓글을 입력해주세요\"}, {\"success\":false,\"message\":\"[오류내용]\"}]")))
    })
    @PutMapping("/api/user/comments")
    public ResponseEntity<?> comment_update(@Valid @RequestBody UserUpdateCommentDto updateCommentDto, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,bindingResult.getFieldError().getDefaultMessage()));
            }

            commentService.update(updateCommentDto.getMemberId(),updateCommentDto.getCommentId(),updateCommentDto.getCommentContent());
            Comment comment = commentService.findById(updateCommentDto.getCommentId());
            UserUpdateCommentDto userUpdateCommentDto = new UserUpdateCommentDto(comment.getCommentContent(), comment.getMember().getId(), comment.getId(), comment.getUpdated_at());

            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 수정 성공",userUpdateCommentDto));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());
        }
        catch (CommentException e){
            throw new CommentException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
