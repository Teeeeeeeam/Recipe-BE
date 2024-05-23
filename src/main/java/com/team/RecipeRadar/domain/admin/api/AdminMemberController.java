package com.team.RecipeRadar.domain.admin.api;

import com.team.RecipeRadar.domain.admin.application.AdminService;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.post.application.PostService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "어드민 관련 컨트롤러" ,description = "어드민 페이지의 관련 API")
@RequestMapping("/api/admin")
public class AdminMemberController {

    private final AdminService adminService;
    private final PostService postService;




    @Operation(summary = "회원수 조회 API", description = "현재 가입된 회원수를 조회하는 API")
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

    @Operation(summary = "게시글수 조회 API", description = "작성된 게시글의 수를 조회하는 API")
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

    @Operation(summary = "레시피수 조회 API", description = "작성된 레시피의 수를 조회하는 API")
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

    @Operation(summary = "사용자 조회 API", description = "가입된 회원의 사용자를 모두 조회하는 API(무한 스크롤방식)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfos\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/info")
    public ResponseEntity<?> getMemberInfos(@RequestParam(value = "member-id",required = false) Long memberId ,Pageable pageable){
        MemberInfoResponse memberInfoResponse = adminService.memberInfos(memberId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",memberInfoResponse));
    }

    @Operation(summary = "사용자 탈퇴 API", description = "사용자의 강제 탈퇴 시킬수 있으며, 여려명 사용자의 대해서도 일괄 삭제가 가능하다. API 사용자가 이용했던 모든 데이터를 삭제한다.")
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
            adminService.adminDeleteUsers(memberIds);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException("사용자를 찾을수 없습니다.");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류");
        }
    }

    @Operation(summary = "사용자 검색 API", description = "가입된 회원의 이름, 아이디, 닉네임, 이메일을 통해 사용자를 조회하는 API(무한 스크롤방식, 아이디,이름,닉네임, 이메일 하나라도 일치하는 사용자를 출력 no like 문, 아무 데이터도 안넘기면 모든 사용자를 출력)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfos\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/search")
    public ResponseEntity<?> searchMember(@RequestParam(value = "login-id",required = false) String loginId,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) String email,
                                          @RequestParam(required = false) String nickname,
                                          @RequestParam(value = "member-id",required = false) Long memberId,
                                          Pageable pageable){
        MemberInfoResponse memberInfoResponse = adminService.searchMember(loginId, nickname, email, username, memberId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",memberInfoResponse));
    }


    @Operation(summary = "요리글 일괄 삭제 API",description = "작성한 사용자만이 해당 레시피를 삭제가능 삭제시 해당 게시물과 관련된 데이터는 모두 삭제")
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

    @Operation(summary = "게시글의 작성된 댓글 조회", description = "해당 게시글의 작성된 댓글을 모두 조회하는 API(무한 스크롤방식)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":false,\"comment\":[{\"id\":16,\"comment_content\":\"댓글 내용 1\",\"member\":{\"nickname\":\"User2\",\"loginId\":\"user1\",\"username\":\"실명\"},\"create_at\":\"2024-05-23T17:37:53\"},{\"id\":17,\"comment_content\":\"댓글 내용 2\",\"member\":{\"nickname\":\"User2\",\"loginId\":\"user1\",\"username\":\"실명\"},\"create_at\":\"2024-05-23T17:37:53\"}]}}"
                            ))),
    })
    @GetMapping("/posts/comments")
    public ResponseEntity<?> getPostsContainsComments( @RequestParam("post-id") Long postId,@RequestParam(value = "last-id",required = false)Long lastId, Pageable pageable){
        PostsCommentResponse postsComments = adminService.getPostsComments(postId, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",postsComments));
    }

    @Operation(summary = "게시글 댓글 일괄 삭제 API",description = "댓글을 단일,일괄 삭제 가능 어드민 사용자만 가능")
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
}
