package com.team.RecipeRadar.domain.post.api;

import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.exception.PostException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "일반 사용자 요리글 컨트롤러", description = "일반 사용자 관련 요리글 작업")
})
@Slf4j
public class PostController {

    private final PostService postService;

    @Operation(summary = "요리글 작성 API", description = "로그인한 사용자만 요리글 작성 가능", tags = {"일반 사용자 요리글 컨트롤러"} )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"모든 값을 입력해 주세요\", \"data\": {\"postCookingTime\": \"요리 시간을 선택하세요\"}}"))),
    })
    @PostMapping(value = "/api/user/posts",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postAdd(@Valid @RequestPart UserAddRequest userAddPostDto, BindingResult bindingResult, @RequestPart MultipartFile file) {
        try {
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;
            postService.save(userAddPostDto,file);
            return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));
        }catch (NoSuchElementException e){
            throw new PostException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "전체 요리글 조회 API", description = "모든 사용자가 해당 게시글의 페이지를 볼수있음(무한페이징)", tags = {"일반 사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)),
                        examples = @ExampleObject(value = "{\"nextPage\":false,\"posts\":[{\"id\":5,\"postTitle\":\"맛있는 파스타 레시피\",\"nickName\":\"짱파스타\",\"postImageUrl\":\"http://example.com/pasta.jpg\"},{\"id\":4,\"postTitle\":\"맛있는 파스타 레시피\",\"nickName\":\"짱파스타\",\"postImageUrl\":\"http://example.com/pasta.jpg\"}]}"))),
    })
    @GetMapping("/api/posts")
    public ResponseEntity<?> findAllPosts(Pageable pageable) {
        PostResponse postResponse = postService.postPage(pageable);
        return ResponseEntity.ok(postResponse);
    }

    @Operation(summary = "요리글 상세 조회 API", description = "사용자가 요리글의 상세 정보를 조회할 수 있습니다.", tags = {"일반 사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = PostResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회성공\",\"data\":{\"post\":{\"id\":3,\"postTitle\":\"냉장고~\",\"postContent\":\"이 파스타는 정말 간단하고 맛있어요!\",\"nickName\":\"김민우랍니다\",\"create_at\":\"2024-05-20T01:24:07.748424\",\"postServing\":\"3인분\",\"postCookingTime\":\"30분\",\"postCookingLevel\":\"중\",\"postLikeCount\":0,\"postImageUrl\":\"http://example.com/pasta.jpg\"},\"comments\":[{\"id\":1,\"comment_content\":\"댓글 작성!\",\"nickName\":\"닉네임\",\"create_at\":\"2024-05-20T02:26:00\"}]}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"해당하는 게시물이 없습니다.\"}")))
    })
    @GetMapping("/api/user/posts/{post-id}")
    public  ResponseEntity<?> findPost(@PathVariable("post-id") long id) {
        try {
            PostDetailResponse postDetailResponse = postService.postDetail(id);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회성공",postDetailResponse));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }

    }

    @Operation(summary = "요리글 삭제 API",description = "작성한 사용자만이 해당 레시피를 삭제가능 삭제시 해당 게시물과 관련된 데이터는 모두 삭제",tags = {"일반 사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"요리글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"게시글을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))

    })
    @DeleteMapping("/api/user/posts/{post-id}")
    public ResponseEntity<?> deletePost(@PathVariable("post-id") Long postId){
        try{
            String loginId = authenticationLogin();
            postService.delete(loginId,postId);
            return ResponseEntity.ok(new ControllerApiResponse(true,"게시글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }


    @Operation(summary = "요리글 수정 API", description = "로그인한 사용자만 수정이 가능하며 작성자만 수정이 가능하도록 이전에 비밀번호 검증을 통해서 검증확인해 해당 API 접근가능", tags = {"일반 사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"요리글 수정 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"게시글을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))
    })
    @PostMapping(value = "/api/user/update/posts",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> updatePost(@Valid @RequestPart UserUpdateRequest updatePostDto, BindingResult bindingResult,@RequestPart MultipartFile file){
        try{
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;

            String loginId = authenticationLogin();
            postService.update(updatePostDto,loginId,file);

            return ResponseEntity.ok(new ControllerApiResponse(true,"요리글 수정 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }


    @Operation(summary = "게시글 비밀번호 검증 API",description = "게시글 삭제 수정시 해당 메소드를 통해 게시글 작성시 입력한 비밀번호의 대해서 검증 성공시에만 수정,삭제가 가능하도록",tags = {"일반 사용자 요리글 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"비밀번호 인증 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"비밀번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성한 사용자만 가능합니다.\"}")))

    })
    @PostMapping("/api/valid/posts")
    public ResponseEntity<?> validPost(@RequestBody ValidPostRequest request){
        try {
            String login = authenticationLogin();
            boolean valid = postService.validPostPassword(login, request);
            return ResponseEntity.ok(new ControllerApiResponse<>(valid,"비밀번호 인증 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
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

    //로그인한 사용자의 loginId를 스프링 시큐리티에서 획득
    private static String authenticationLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String loginId = principal.getMemberDto(principal.getMember()).getLoginId();
        return loginId;
    }

    /*
    BindingResult 의 예외 Valid 여러곳의 사용되어서 메소드로 추출 
     */
    private static ResponseEntity<ErrorResponse<Map<String, String>>> getErrorResponseResponseEntity(BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            Map<String,String> errorMap = new HashMap<>();
            for(FieldError error : bindingResult.getFieldErrors()){
                errorMap.put(error.getField(),error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "모든 값을 입력해 주세요", errorMap));
        }
        return null;
    }
}