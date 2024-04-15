package com.team.RecipeRadar.domain.post.api;

import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostResponse;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.user.UserAddPostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserDeletePostDto;
import com.team.RecipeRadar.domain.post.dto.user.UserUpdatePostDto;
import com.team.RecipeRadar.domain.post.exception.PostException;
import com.team.RecipeRadar.domain.post.exception.ex.PostNotFoundException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
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
    public ResponseEntity<?> postAdd(@Valid @RequestBody UserAddPostDto userAddPostDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }
            Post save = postService.save(userAddPostDto);

            UserAddPostDto addResponse = new UserAddPostDto(
                    save.getPostTitle(),
                    save.getPostContent(),
                    save.getMember().getId(),
                    save.getPostServing(),
                    save.getPostCookingTime(),
                    save.getPostCookingLevel(),
                    save.getPostImageUrl());

            return ResponseEntity.ok(new ControllerApiResponse(true,"성공", addResponse));
        }catch (NoSuchElementException e){
            throw new PostException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "전체 요리글 조회 API", description = "전체 사용자만 전체를 조회할 수 있습니다.", tags = {"사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)),
                        examples = @ExampleObject(value = "[{\"postId\": \"[게시글 ID]\", \"postTitle\": \"[요리글 제목]\", \"postContent\": \"[요리글 내용]\", \"memberId\": \"[사용자 ID]\", \"created_at\": \"LocalDateTime\"}]"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/posts")
    public  ResponseEntity<List<PostResponse>> findAllPosts() {
        List<PostResponse> posts = postService.findAll()
                .stream()
                .map(PostResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(posts);
    }

    @Operation(summary = "요리글 상세 조회 API", description = "사용자가 요리글의 상세 정보를 조회할 수 있습니다.", tags = {"사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = PostResponse.class),
                            examples = @ExampleObject(value = "{\"postId\": \"[게시글 ID]\", \"postTitle\": \"[요리글 제목]\", \"postContent\": \"[요리글 내용]\", \"memberId\": \"[사용자 ID]\", \"created_at\": \"LocalDateTime\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    public ResponseEntity<?> deletePost(@RequestBody UserDeletePostDto userDeletePostDto){
        try{
            postService.delete(userDeletePostDto);
            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new PostNotFoundException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }

    @Operation(summary = "요리글 수정 API", description = "로그인, 작성자만 수정가능", tags = {"일반 사용자 댓글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"postTitle\": \"[수정한 요리글 제목]\", \"postContent\": \"[수정한 요리글 내용]\", \"postServing\": \"[수정한 요리 제공 인원]\", \"postCookingTime\": \"[수정한 요리 소요 시간]\", \"postCookingLevel\": \"[수정한 요리 난이도]\", \"postImageUrl\": \"[수정한 이미지 URL]\", \"memberId\": \"[사용자 ID]\", \"postId\": \"[게시글 ID]\", \"update_At\": \"[수정 시간]\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/api/user/post/update")
    public  ResponseEntity<?> updatePost(@Valid @RequestBody UserUpdatePostDto updatePostDto, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }

            postService.update(
                    updatePostDto.getMemberId(),
                    updatePostDto.getPostId(),
                    updatePostDto.getPostTitle(),
                    updatePostDto.getPostContent(),
                    updatePostDto.getPostServing(),
                    updatePostDto.getPostCookingTime(),
                    updatePostDto.getPostCookingLevel(),
                    updatePostDto.getPostImageUrl()
            );
            Post post = postService.findById(updatePostDto.getPostId());
            UserUpdatePostDto userUpdatePostDto = new UserUpdatePostDto(
                    post.getPostTitle(),
                    post.getPostContent(),
                    post.getMember().getId(),
                    post.getId(),
                    post.getPostServing(),
                    post.getPostCookingTime(),
                    post.getPostCookingLevel(),
                    post.getPostImageUrl()
            );

            return ResponseEntity.ok(new ControllerApiResponse(true,"요리글 수정 성공", userUpdatePostDto));
        }catch (NoSuchElementException e){
            throw new PostNotFoundException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "작성한 게시글 조회",description = "사용자가 작성한 게시글을 조회하는 API, 기본으로 작성한 게시글을 최신순으로 DESC 정렬, SORT 사용X  (현재 총 작성한 게시물 수의 대해서는 적용x  필요시에 추가 가능)",tags = {"사용자 페이지 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                    examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":\"boolean\",\"content\":[{\"id\":\"[게시글 id]\", \"postTitle\" :\"[게시글 제목]\"}]}}"))),
            @ApiResponse(responseCode = "401",description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"접근할 수 없는 사용자입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"쿠키값이 없을때 접근\"}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
                    content =@Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/user/info/{login-id}/posts")
    public ResponseEntity<?> postTitlePage(@PathVariable("login-id") String loginId, @CookieValue(name = "login-id",required = false) String cookieLoginId,Pageable pageable){
        try {
            if (cookieLoginId ==null){
                throw new ForbiddenException("쿠키값이 없을때 접근");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticationName = authentication.getName();
            UserInfoPostResponse userInfoPostResponse = postService.userPostPage(authenticationName, loginId, pageable);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",userInfoPostResponse));
        }catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }
    }
}