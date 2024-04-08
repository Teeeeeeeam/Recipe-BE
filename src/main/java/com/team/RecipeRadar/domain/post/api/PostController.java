package com.team.RecipeRadar.domain.post.api;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.PostResponse;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;
import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.domain.post.dto.user.UserAddPostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserDeletePostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserUpdatePostDto;
import com.team.RecipeRadar.domain.post.exception.ex.PostNotFoundException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.CommentException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "어드민 요리글 컨트롤러", description = "어드민 관련 요리글 작업"),
        @Tag(name = "일반 사용자 요리글 컨트롤러", description = "일반 사용자 관련 요리글 작업")
})
@Slf4j
public class PostController {

    private final PostService postService;

    @Operation(summary = "요리글 작성 API", description = "로그인한 사용자만 요리글 작성 가능", tags = {"사용자 요리글 컨트롤러"} )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"postContent\": \"[작성한 요리글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"created_at\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/api/user/post/add")
    public ResponseEntity<?> post_add(@RequestBody UserAddPostDto userAddPostDto) {
        try {
            Post save = postService.save_post(userAddPostDto);

            UserAddPostDto addResponse = new UserAddPostDto(save.getPostTitle(), save.getPostContent(), save.getMember().getId(), save.getId(), save.getCreated_at());

            return ResponseEntity.ok(new ControllerApiResponse(true,addResponse));
        }catch (NoSuchElementException e){
            throw new CommentException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @GetMapping("/api/posts")
    public  ResponseEntity<List<PostResponse>> findAllPosts() {
        List<PostResponse> posts = postService.findAll()
                .stream()
                .map(PostResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(posts);
    }
    @GetMapping("api/posts/{id}")
    public  ResponseEntity<PostResponse> findPost(@PathVariable long id) {
        Post post = postService.findById(id);

        return  ResponseEntity.ok()
                .body(new PostResponse(post));
    }

    @Operation(summary = "요리글 삭제 API",description = "로그인한 사용자만 요리글 삭제가능",tags = {"일반 사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"요리글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

    })
    @DeleteMapping("/api/user/post/delete")
    public ResponseEntity<?> delete_post(@RequestBody UserDeletePostDto userDeletePostDto){
        try{
            postService.delete_post(userDeletePostDto);
            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new PostNotFoundException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }

    @Operation(summary = "요리글 수정 API",description = "로그인, 작성자만 수정가능",tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"commentContent\": \"[수정한 요리글]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"update_At\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    @PutMapping("/api/user/post/update")
    public  ResponseEntity<?> update_post(@RequestBody UserUpdatePostDto updatePostDto){
        try{
            postService.update_post((updatePostDto.getMemberId(), updatePostDto.getPostId()));
            UserUpdatePostDto userUpdatePostDto = new UserUpdatePostDto(post.getPostTitle(), post.getMember.getId(), post.getId(), post.getUpdated_at());

            return ResponseEntity.ok(new ControllerApiResponse(true,userUpdatePostDto));
        }catch (NoSuchElementException e){
            throw new PostNotFoundException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
    }
}
    }
}
