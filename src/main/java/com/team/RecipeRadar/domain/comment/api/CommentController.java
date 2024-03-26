package com.team.RecipeRadar.domain.comment.api;

import com.team.RecipeRadar.domain.comment.application.CommentService;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.AddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.CommentResponse;
import com.team.RecipeRadar.domain.comment.dto.UpdateCommentRequest;
import com.team.RecipeRadar.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.global.exception.ex.CommentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.server.ServerErrorException;

import java.util.NoSuchElementException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/api/admin/comments")
    public ResponseEntity<Comment> addComment(@RequestBody AddCommentRequest request) {
        Comment savedComment = commentService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedComment);
    }

    @GetMapping("/api/admin/comments")
    public  ResponseEntity<List<CommentResponse>> findAllComments() {
        List<CommentResponse> comments = commentService.findAll()
                .stream()
                .map(CommentResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(comments);
    }
    @GetMapping("api/admin/comments/{id}")
    public  ResponseEntity<CommentResponse> findComment(@PathVariable long id) {
        Comment comment = commentService.findById(id);

        return  ResponseEntity.ok()
                .body(new CommentResponse(comment));
    }

    @DeleteMapping("/api/admin/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable long id) {
        commentService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/admin/comments/{id}")
    public  ResponseEntity<Comment> updateComment(@PathVariable long id, @RequestBody UpdateCommentRequest request){
        Comment updateComment = commentService.update(id, request);

        return ResponseEntity.ok()
                .body(updateComment);
    }

    @GetMapping("/api/comments/search")
    public ResponseEntity<List<CommentResponse>> searchComment(@RequestParam String query) {
        List<Comment> comments = commentService.searchComments(query);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(commentResponses);
    }
    @PostMapping("/api/user/comment/add")
    public ResponseEntity<?> comment_add(@RequestBody CommentDto commentDto){
        try {
            Comment save = commentService.save(commentDto);
            return ResponseEntity.ok(new ApiResponse(true,"댓글 등록성공"));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @DeleteMapping("/api/user/comment/delete")
    public ResponseEntity<?> comment_delete(@RequestBody CommentDto commentDto){
        try{
           commentService.delete_comment(commentDto);//반환타입 void
            return ResponseEntity.ok(new ApiResponse(true,"댓글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }

    @GetMapping("/api/comment")
    public ResponseEntity<?> comment_Page(@PageableDefault Pageable pageable,
                                          @RequestParam(value = "posts",required = false)String postid){
        try {
            Page<CommentDto> comments = commentService.commentPage(Long.parseLong(postid), pageable);
            return ResponseEntity.ok(comments);
        }catch (CommentException e){
          throw new CommentException(e.getMessage());
        } catch (Exception e){
            throw new ServerErrorException(e.getMessage());
        }
    }

    @PutMapping("/api/user/update")
    public ResponseEntity<?> comment_update(@RequestBody CommentDto commentDto){
        try {
            commentService.update(commentDto.getMemberDto().getId(),commentDto.getId(),commentDto.getComment_content());
            return ResponseEntity.ok(new ApiResponse(true,"댓글 수정 성공"));
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
