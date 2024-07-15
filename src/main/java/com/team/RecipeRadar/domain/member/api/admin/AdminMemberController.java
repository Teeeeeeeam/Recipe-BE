package com.team.RecipeRadar.domain.member.api.admin;

import com.team.RecipeRadar.domain.blackList.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.application.admin.AdminMemberService;
import com.team.RecipeRadar.domain.email.event.MailEvent;
import com.team.RecipeRadar.global.payload.ErrorResponse;
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
public class AdminMemberController {

    private final AdminMemberService adminMemberService;
    private final ApplicationEventPublisher eventPublisher;


    @Tag(name = "어드민 - 회원 및 블랙리스트 관리",description = "회원 관리")
    @Operation(summary = "회원수 조회", description = "현재 가입된 회원의 수를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":10}"))),
    })
    @GetMapping("/members/count")
    public ResponseEntity<ControllerApiResponse> getAllMembersCount(){
        long searchAllMembers = adminMemberService.searchAllMembers();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }

    @Operation(summary = "사용자 조회", description = "가입된 회원의 정보를 모두 조회하는 API(무한 스크롤 방식)",tags = "어드민 - 회원 및 블랙리스트 관리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"memberInfoes\":[{\"id\":1,\"username\":\"일반\",\"nickname\":\"일반사용자\",\"loginId\":\"user1234\",\"email\":\"user@user.com\"},{\"id\":2,\"username\":\"관리자\",\"nickname\":\"어드민\",\"loginId\":\"admin1234\",\"email\":\"admin@admin.com\"}],\"nextPage\":false}}"))),
    })
    @GetMapping("/members/info")
    public ResponseEntity<ControllerApiResponse> getMemberInfos(@RequestParam(value = "lastId",required = false) Long lastMemberId ,
                                                                @Parameter(example = "{\"size\":10}") Pageable pageable){
        MemberInfoResponse memberInfoResponse = adminMemberService.memberInfos(lastMemberId,pageable);
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
        List<String> emailList = adminMemberService.adminDeleteUsers(memberIds);

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
        MemberInfoResponse memberInfoResponse = adminMemberService.searchMember(loginId, nickname, email, username, memberId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",memberInfoResponse));
    }

}
