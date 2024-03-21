package com.team.RecipeRadar.domain.comment.api;

import com.team.RecipeRadar.domain.comment.application.CommentService;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.AddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.CommentResponse;
import com.team.RecipeRadar.domain.comment.dto.UpdateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
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
}
