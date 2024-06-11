package com.team.RecipeRadar.domain.admin.api;

import com.team.RecipeRadar.domain.admin.application.AdminService;
import com.team.RecipeRadar.domain.admin.dto.BlackListResponse;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.global.event.email.MailEvent;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminMemberController {

    private final AdminService adminService;
    private final PostService postService;
    private final ApplicationEventPublisher eventPublisher;

    @Tag(name = "어드민 - 회원 및 블랙리스트 관리",description = "회원 관리")
    @Operation(summary = "회원수 조회", description = "현재 가입된 회원의 수를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":\"10\"}"))),
    })
    @GetMapping("/members/count")
    public ResponseEntity<?> getAllMembersCount(){
        long searchAllMembers = adminService.searchAllMembers();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }

    @Tag(name = "어드민 - 게시글 컨트롤러",description = "게시글 관리")
    @Operation(summary = "게시글수 조회", description = "작성된 게시글의 수를 조회하는 API",tags = "어드민 - 게시글 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":\"10\"}"))),
    })
    @GetMapping("/posts/count")
    public ResponseEntity<?> getAllPostsCount(){
        long searchAllMembers = adminService.searchAllPosts();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }

    @Operation(summary = "레시피수 조회", description = "작성된 레시피의 수를 조회하는 API",tags = "어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":\"10\"}"))),
    })
    @GetMapping("/recipes/count")
    public ResponseEntity<?> getAllRecipesCount(){
        long searchAllMembers = adminService.searchAllRecipes();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }

    // TODO: 2024-06-11 member-id를 last-id로 통일
    @Operation(summary = "사용자 조회", description = "가입된 회원의 정보를 모두 조회하는 API(무한 스크롤 방식)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfos\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/info")
    public ResponseEntity<?> getMemberInfos(@RequestParam(value = "member-id",required = false) Long memberId ,
                                            @Parameter(example = "{\"size\":10}")Pageable pageable){
        MemberInfoResponse memberInfoResponse = adminService.memberInfos(memberId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",memberInfoResponse));
    }

    @Operation(summary = "사용자 추방", description = "사용자를 강제 탈퇴시키는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"삭제 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"사용자를 찾을수 없습니다.\"}")))
    })
    @DeleteMapping("/members")
    public ResponseEntity<?> deleteAllUser(@RequestParam("ids") List<Long> memberIds){
        try {
            List<String> emailList = adminService.adminDeleteUsers(memberIds);

            for (String email : emailList) {;
                eventPublisher.publishEvent(new MailEvent(email));
            }
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException("사용자를 찾을수 없습니다.");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류");
        }
    }

    @Operation(summary = "사용자 검색", description = "가입된 회원의 정보를 검색하는 API(무한 스크롤 방식)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfos\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/search")
    public ResponseEntity<?> searchMember(@Schema(example = "admin1234")@RequestParam(value = "login-id",required = false) String loginId,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) String email,
                                          @RequestParam(required = false) String nickname,
                                          @RequestParam(value = "member-id",required = false) Long memberId,
                                          @Parameter(example = "{\"size\":10}")Pageable pageable){
        MemberInfoResponse memberInfoResponse = adminService.searchMember(loginId, nickname, email, username, memberId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",memberInfoResponse));
    }

    @Operation(summary = "요리글 삭제",description = "요리글을 단일, 일괄 삭제하는 API",tags = "어드민 - 게시글 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"게시글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"게시글을 찾을수 없습니다.\"}"))),
    })
    @DeleteMapping("/posts")
    public ResponseEntity<?> deletePost(@RequestParam(value = "ids") List<Long> postIds){
        try{
            postService.deletePosts(postIds);
            return ResponseEntity.ok(new ControllerApiResponse(true,"게시글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류");
        }
    }

    @Operation(summary = "게시글의 작성된 댓글 조회", description = "게시글의 작성된 댓글을 조회하는 API",tags = "어드민 - 게시글 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":false,\"comment\":[{\"id\":16,\"comment_content\":\"댓글 내용 1\",\"member\":{\"nickname\":\"User2\",\"loginId\":\"user1\",\"username\":\"실명\"},\"create_at\":\"2024-05-23T17:37:53\"},{\"id\":17,\"comment_content\":\"댓글 내용 2\",\"member\":{\"nickname\":\"User2\",\"loginId\":\"user1\",\"username\":\"실명\"},\"create_at\":\"2024-05-23T17:37:53\"}]}}"))),
    })
    @GetMapping("/posts/comments")
    public ResponseEntity<?> getPostsContainsComments( @RequestParam("post-id") Long postId,@RequestParam(value = "last-id",required = false)Long lastId,
                                                       @Parameter(example = "{\"size\":10}") Pageable pageable){
        PostsCommentResponse postsComments = adminService.getPostsComments(postId, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",postsComments));
    }

    @Operation(summary = "게시글 댓글 삭제",description = "게시글의 댓글을 단일, 일괄 삭제하는 API",tags = "어드민 - 게시글 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"댓글 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"댓글을 찾을수 없습니다.\"}"))),
    })
    @DeleteMapping("/posts/comments")
    public ResponseEntity<?> deleteComments(@RequestParam(value = "ids") List<Long> commentsIds){
        try{
            adminService.deleteComments(commentsIds);
            return ResponseEntity.ok(new ControllerApiResponse(true,"댓글 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류");
        }
    }

    @Operation(summary = "레시피 삭제",description = "레시피를 단일, 일괄 삭제하는 API",tags = "어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"레시피 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"해당 레시피를 찾을수 없습니다.\"}"))),

    })
    @DeleteMapping("/recipes")
    public ResponseEntity<?> deleteRecipe(@RequestParam(value = "ids") List<Long> recipeIds){
        try{
            adminService.deleteRecipe(recipeIds);
            return ResponseEntity.ok(new ControllerApiResponse(true,"레시피 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            throw new ServerErrorException("서버 오류");
        }
    }

    @Operation(summary = "블랙 리스트 이메일 조회",description = "블랙 리스트에 등록된 이메일 목록을 조회하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"blackList\":[{\"id\":1,\"email\":\"user1@example.com\",\"black_check\":true}]}}")))
    })
    @GetMapping("/black")
    public ResponseEntity<?> getBlackList(@RequestParam(name = "last-id",required = false) Long lastId,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable){
        BlackListResponse blackList = adminService.getBlackList(lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",blackList));
    }

    @Operation(summary = "블랙 리스트 이메일 차단 유뮤",description = "블랙 리스트에 등록된 이메일의 차단 해제 유무를 설정하는 API(false - 임시 차단 해제 , ture - 임시 차단)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"임시 차단 해제\"}")))
    })
    @PostMapping("/blacklist/temporary-unblock/{id}")
    public ResponseEntity<?> unBlock(@Schema(example = "1")@PathVariable Long id){
        boolean unblockUser = adminService.temporarilyUnblockUser(id);
        return ResponseEntity.ok(new ControllerApiResponse<>(unblockUser,"임시 차단 유뮤"));
    }

    @Operation(summary = "블랙 리스트 이메일 해제",description = "블랙 리스트에 등록된 이메일의 차단을 해제하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"삭제 성공\"}")))
    })
    @DeleteMapping("/blacklist/{id}")
    public ResponseEntity<?> deleteBlack(@PathVariable Long id){
        adminService.deleteBlackList(id);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }
        
}
