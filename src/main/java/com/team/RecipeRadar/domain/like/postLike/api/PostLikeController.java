package com.team.RecipeRadar.domain.like.postLike.api;

import com.team.RecipeRadar.domain.like.ex.LikeException;
import com.team.RecipeRadar.domain.like.postLike.application.PostLikeService;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;
import com.team.RecipeRadar.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@Slf4j
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/api/user/postLike")
    public ResponseEntity<?> addLike(@RequestBody PostLikeDto postLikeDto,HttpServletRequest request){
        try {
            Boolean aBoolean = postLikeService.addLike(postLikeDto);
            ApiResponse response;
            if (!aBoolean){
                response = new ApiResponse(true,"좋아요 성공");
            }else
                response = new ApiResponse(true, "좋아요 해제");
            return ResponseEntity.ok(response);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            throw new LikeException(e.getMessage());
        }catch (Exception e){
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @GetMapping("/api/likeCheck")
    public ResponseEntity<?> likeCheck(@RequestParam(value = "postId",required = false) String postId, HttpServletRequest request){
        try {
            String header = request.getHeader("Authorization");
            Boolean aBoolean = false;
            if (header!=null) {
                String jwtToken = header.replace("Bearer ", "");
              aBoolean = postLikeService.checkLike(jwtToken, Long.parseLong(postId));
            }
            return ResponseEntity.ok(new ApiResponse(aBoolean,"좋아요 상태"));
        }catch (Exception e){
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
