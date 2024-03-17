package com.team.RecipeRadar.controller;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Service.CommentService;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.exception.ex.CommentException;
import com.team.RecipeRadar.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
            throw new CommentException("회원정보나 게시글을 찾을수 없습니다.");
        }catch (Exception e){
            throw new ServerErrorException("서버오류");
        }

    }

}
