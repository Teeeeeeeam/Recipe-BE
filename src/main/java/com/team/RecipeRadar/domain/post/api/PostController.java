package com.team.RecipeRadar.domain.post.api;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.PostResponse;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;
import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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

import java.util.List;

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
    public ResponseEntity<Post> addPost(@RequestBody AddPostRequest request) {
        Post savedPost = postService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPost);
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

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id) {
        postService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/api/admin/posts/{id}")
    public ResponseEntity<Void> adminDeletePost(@PathVariable long id) {
        postService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/posts/{id}")
    public  ResponseEntity<Post> updatePost(@PathVariable long id, @RequestBody UpdatePostRequest request){
        Post updatePost = postService.update(id, request);

        return ResponseEntity.ok()
                .body(updatePost);
    }
}
