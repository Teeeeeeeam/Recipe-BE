package com.team.RecipeRadar.controller;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Service.CommentService;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.exception.ex.CommentException;
import com.team.RecipeRadar.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/user/comment/add")
    public ResponseEntity<?> comment_add(@RequestBody CommentDto commentDto){
        try {
            Comment save = commentService.save(commentDto);
            return ResponseEntity.ok(new ApiResponse(true,save));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());
        }catch (Exception e){
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



}
