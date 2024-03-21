package com.team.RecipeRadar.domain.post.api;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.PostResponse;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;
import com.team.RecipeRadar.domain.post.application.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/api/admin/posts")
    public ResponseEntity<Post> addPost(@RequestBody AddPostRequest request) {
        Post savedPost = postService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPost);
    }

    @GetMapping("/api/admin/posts")
    public  ResponseEntity<List<PostResponse>> findAllPosts() {
        List<PostResponse> posts = postService.findAll()
                .stream()
                .map(PostResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(posts);
    }
    @GetMapping("api/admin/posts/{id}")
    public  ResponseEntity<PostResponse> findPost(@PathVariable long id) {
        Post post = postService.findById(id);

        return  ResponseEntity.ok()
                .body(new PostResponse(post));
    }

    @DeleteMapping("/api/admin/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id) {
        postService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/admin/posts/{id}")
    public  ResponseEntity<Post> updatePost(@PathVariable long id, @RequestBody UpdatePostRequest request){
        Post updatePost = postService.update(id, request);

        return ResponseEntity.ok()
                .body(updatePost);
    }
}
