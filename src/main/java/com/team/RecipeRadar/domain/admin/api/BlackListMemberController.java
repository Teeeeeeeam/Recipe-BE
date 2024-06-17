package com.team.RecipeRadar.domain.admin.api;

import com.team.RecipeRadar.domain.admin.application.blackMember.AdminBlackMemberService;
import com.team.RecipeRadar.domain.admin.dto.BlackListResponse;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.global.event.email.MailEvent;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class BlackListMemberController {

    private final AdminBlackMemberService blackMemberService;
    private final ApplicationEventPublisher eventPublisher;


    @Tag(name = "어드민 - 회원 및 블랙리스트 관리",description = "회원 관리")
    @Operation(summary = "회원수 조회", description = "현재 가입된 회원의 수를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":\"10\"}"))),
    })
    @GetMapping("/members/count")
    public ResponseEntity<ControllerApiResponse> getAllMembersCount(){
        long searchAllMembers = blackMemberService.searchAllMembers();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }


    @Operation(summary = "블랙 리스트 이메일 조회",description = "블랙 리스트에 등록된 이메일 목록을 조회하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"blackList\":[{\"id\":1,\"email\":\"user1@example.com\",\"black_check\":true}]}}")))
    })
    @GetMapping("/black")
    public ResponseEntity<ControllerApiResponse> getBlackList(@RequestParam(name = "lastId",required = false) Long lastId,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable){
        BlackListResponse blackList = blackMemberService.getBlackList(lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",blackList));
    }


    @Operation(summary = "블랙 리스트 이메일 차단 유뮤",description = "블랙 리스트에 등록된 이메일의 차단 해제 유무를 설정하는 API(false - 임시 차단 해제 , ture - 임시 차단)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"임시 차단 해제\"}")))
    })
    @PostMapping("/blacklist/temporary-unblock/{blackId}")
    public ResponseEntity<ControllerApiResponse> unBlock(@Schema(example = "1")@PathVariable(value = "blackId") Long blackId){
        boolean unblockUser = blackMemberService.temporarilyUnblockUser(blackId);
        return ResponseEntity.ok(new ControllerApiResponse<>(unblockUser,"임시 차단 유뮤"));
    }


    @Operation(summary = "블랙 리스트 이메일 해제",description = "블랙 리스트에 등록된 이메일의 차단을 해제하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"삭제 성공\"}")))
    })
    @DeleteMapping("/blacklist/{blackId}")
    public ResponseEntity<ControllerApiResponse> deleteBlack(@PathVariable(value = "blackId") Long blackId){
        blackMemberService.deleteBlackList(blackId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }

    @Operation(summary = "사용자 조회", description = "가입된 회원의 정보를 모두 조회하는 API(무한 스크롤 방식)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfoes\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/info")
    public ResponseEntity<ControllerApiResponse> getMemberInfos(@PathVariable(value = "lastId",required = false) Long lastMemberId ,
                                            @Parameter(example = "{\"size\":10}")Pageable pageable){
        MemberInfoResponse memberInfoResponse = blackMemberService.memberInfos(lastMemberId,pageable);
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
    public ResponseEntity<ControllerApiResponse> deleteAllUser(@RequestParam("memberIds") List<Long> memberIds){
        List<String> emailList = blackMemberService.adminDeleteUsers(memberIds);

        for (String email : emailList)
            eventPublisher.publishEvent(new MailEvent(email));

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }

    @Operation(summary = "사용자 검색", description = "가입된 회원의 정보를 검색하는 API(무한 스크롤 방식)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfoes\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/search")
    public ResponseEntity<ControllerApiResponse> searchMember(@Schema(example = "admin1234")@RequestParam(value = "loginId",required = false) String loginId,
                                          @RequestParam(value = "username",required = false) String username,
                                          @RequestParam(value = "email",required = false) String email,
                                          @RequestParam(value = "nickname",required = false) String nickname,
                                          @RequestParam(value = "memberId",required = false) Long memberId,
                                          @Parameter(example = "{\"size\":10}")Pageable pageable){
        MemberInfoResponse memberInfoResponse = blackMemberService.searchMember(loginId, nickname, email, username, memberId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",memberInfoResponse));
    }

    @Operation(summary = "블랙 리스트 이메일 검색",description = "블랙 리스트에서 이메일을 검색하는 API",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value =  "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":false,\"blackList\":[{\"id\":1,\"email\":\"user1@example.com\",\"blackCheck\":true},{\"id\":2,\"email\":\"user2@example.com\",\"black_check\":true}]}}")))
    })
    @GetMapping("/black/search")
    public ResponseEntity<?> searchEmail(@RequestParam(value = "email",required = false)String email,
                                         @RequestParam(value = "lastId",required = false) Long lastId,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){
        BlackListResponse blackListResponse = blackMemberService.searchEmailBlackList(email, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",blackListResponse));
    }
}
