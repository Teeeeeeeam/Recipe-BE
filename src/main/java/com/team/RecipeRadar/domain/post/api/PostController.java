package com.team.RecipeRadar.domain.post.api;

import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.domain.post.exception.PostException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "사용자 - 게시글 컨트롤러", description = "사용자 게시글과 관련된 API")
@Slf4j
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "로그인한 사용자만 게시글 작성 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"모든 값을 입력해 주세요\", \"data\": {\"postCookingTime\": \"요리 시간을 선택하세요\"}}"))),
    })
    @PostMapping(value = "/api/user/posts",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postAdd(@Valid @RequestPart UserAddRequest userAddPostRequest, BindingResult bindingResult, @RequestPart MultipartFile file) {
        try {
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;
            postService.save(userAddPostRequest,file);
            return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));
        }catch (NoSuchElementException e){
            throw new PostException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "전체 게시글 조회(페이징)", description = "모든 사용자가 해당 게시글의 페이지를 볼 수 있다.(무한페이징)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ControllerApiResponse.class)),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"posts\":[{\"id\":23,\"postTitle\":\"Delicious Pasta\",\"create_at\":\"2024-05-23T14:20:34\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}},{\"id\":24,\"postTitle\":\"Spicy Tacos\",\"create_at\":\"2024-05-23T14:20:34\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}}]}}"))),
    })
    @GetMapping("/api/posts")
    public ResponseEntity<?> findAllPosts(@RequestParam(value = "post-id",required = false) Long postId,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable) {
        PostResponse postResponse = postService.postPage(postId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",postResponse));
    }

    @Operation(summary = "게시글 상세 조회", description = "사용자가 게시글의 상세 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회성공\",\"data\":{\"post\":{\"id\":3,\"postTitle\":\"냉장고~\",\"postContent\":\"이 파스타는 정말 간단하고 맛있어요!\",\"nickName\":\"김민우랍니다\",\"create_at\":\"2024-05-20T01:24:07.748424\",\"postServing\":\"3인분\",\"postCookingTime\":\"30분\",\"postCookingLevel\":\"중\",\"postLikeCount\":0,\"postImageUrl\":\"http://example.com/pasta.jpg\", \"member\":{\"nickname\" :\"Admin\"},\"recipe\":{\"id\":\"123\",\"title\":\"김치\"}},\"comments\":[{\"id\":1,\"comment_content\":\"댓글 작성!\",\"nickName\":\"닉네임\",\"create_at\":\"2024-05-20T02:26:00\"}]}}"))),
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

    @Operation(summary = "게시글 삭제",description = "작성한 사용자만이 해당 레시피를 삭제할 수 있습니다. 삭제 시 해당 게시물과 관련된 모든 데이터가 삭제됩니다.")
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

    @Operation(summary = "게시글 수정",  description = "로그인한 사용자만 수정이 가능하며, 작성자만 수정할 수 있습니다. 비밀번호 검증을 통해 사용자를 확인한 후 해당 API에 접근할 수 있습니다.")
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
    @PostMapping(value = "/api/user/update/posts/{post-id}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> updatePost(@Valid @RequestPart UserUpdateRequest userUpdateRequest, BindingResult bindingResult,
                                         @RequestPart(required = false) MultipartFile file, @PathVariable("post-id") Long postId){
        try{
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;

            String loginId = authenticationLogin();
            postService.update(postId,userUpdateRequest,loginId,file);

            return ResponseEntity.ok(new ControllerApiResponse(true,"요리글 수정 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }


    @Operation(summary = "게시글 비밀번호 검증",description = "게시글 삭제 및 수정 시, 해당 메소드를 통해 게시글 작성 시 입력한 비밀번호를 검증합니다. 검증에 성공한 경우에만 수정 및 삭제가 가능합니다.")
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


    @Operation(summary = "게시글 작성 내역(페이징)",description = "사용자가 작성한 게시글의 무한 페이징입니다.",tags = {"사용자 - 마이페이지 컨트롤러"})
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
    })
    @GetMapping("/api/user/info/{login-id}/posts")
    public ResponseEntity<?> postTitlePage(@PathVariable("login-id") String loginId,
                                           @RequestParam(value = "last-id",required = false) Long lastId,
                                           @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                           @Parameter(example = "{\"size\":10}") Pageable pageable){
        try {
            if (cookieLoginId ==null){
                throw new ForbiddenException("쿠키값이 없을때 접근");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticationName = authentication.getName();
            UserInfoPostResponse userInfoPostResponse = postService.userPostPage(authenticationName,lastId,loginId, pageable);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",userInfoPostResponse));
        }catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }
    }

    @Operation(summary = "게시글 검색",description = "사용자의 로그인 아이디와 게시글 제목, 스크랩한 요리에 대해 검색할 수 있는 API  단일 조건의 검색이 가능하며, 조건 데이터가 추가될 때마다 AND 조건으로 데이터를 추립니다. (무한 페이징)", tags = "어드민 - 게시글 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ControllerApiResponse.class)),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"posts\":[{\"id\":23,\"postTitle\":\"Delicious Pasta\",\"create_at\":\"2024-05-23T14:20:34\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}},{\"id\":24,\"postTitle\":\"Spicy Tacos\",\"create_at\":\"2024-05-23T14:20:34\",\"postImageUrl\":\"https://store_image.jpg\",\"member\":{\"nickname\":\"Admin\",\"loginId\":\"admin\"},\"recipe\":{\"id\":7014704,\"title\":\"아마트리치아나스파게티\"}}]}}"))),
    })
    @GetMapping("/api/search")
    public ResponseEntity<?> searchPost(@RequestParam(value = "login-id",required = false) String loginId,
                                  @RequestParam(value = "recipe-title",required = false) String recipeTitle,
                                  @RequestParam(value = "post-title",required = false) String postTitle,
                                  @RequestParam(value = "post-id",required = false) Long lastPostId,
                                  @Parameter(example = "{\"size\":10}") Pageable pageable){
        PostResponse postResponse = postService.searchPost(loginId, recipeTitle, postTitle, lastPostId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"검색 성공",postResponse));
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