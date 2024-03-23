package com.team.RecipeRadar.domain.like.postLike.api;

import com.team.RecipeRadar.domain.like.ex.LikeException;
import com.team.RecipeRadar.domain.like.postLike.application.PostLikeService;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;
import com.team.RecipeRadar.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;

import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/api/user/postLike")
    public ResponseEntity<?> addLike(@RequestBody PostLikeDto postLikeDto){
        try {
            Boolean aBoolean = postLikeService.addLike(postLikeDto);
            ApiResponse response;
            if (aBoolean){
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
}
