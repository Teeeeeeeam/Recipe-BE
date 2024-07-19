package com.team.RecipeRadar.domain.member.api.user;

import com.team.RecipeRadar.domain.member.application.user.MemberService;
import com.team.RecipeRadar.domain.member.dto.rqeust.UserDeleteIdRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.UserInfoEmailRequest;
import com.team.RecipeRadar.domain.member.dto.response.UserInfoResponse;
import com.team.RecipeRadar.domain.member.dto.rqeust.UserInfoUpdateNickNameRequest;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 - 마이페이지 컨트롤러",description = "사용자 페이지 API 사용자의 정보 및 활동 기록을 확인할 수 있습니다.")
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final CookieUtils cookieUtils;

    @Operation(summary = "회원정보 조회",description = "회원의 회원정보(이름, 닉네임, 이메일, 로그인 타입)를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"조회성공\", \"data\": {\"username\": \"홍길동\", \"nickName\":\"홍길동\", \"email\":\"test@naver.com\",\"loginType\":\"normal\" }}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"사용자를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}")))
    })
    @GetMapping("/user/info")
    public ResponseEntity<?> userInfo(@Parameter(hidden = true) @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                      @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        validCookie(cookieLoginId, principalDetails);

        UserInfoResponse members = memberService.getMembers(principalDetails.getMemberId());

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회성공",members));
    }

    @Operation(summary = "닉네임 변경", description = "회원의 닉네임을 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"변경 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"사용자를 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}")))
    })
    @PutMapping("/user/info/update/nickname")
    public ResponseEntity<?> userInfoNickNameUpdate(@RequestBody UserInfoUpdateNickNameRequest userInfoUpdateNickNameRequest,
                                                    @Parameter(hidden = true)@CookieValue(name = "login-id",required = false) String cookieLoginId,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        validCookie(cookieLoginId, principalDetails);
        memberService.updateNickName(userInfoUpdateNickNameRequest.getNickName(), principalDetails.getMemberId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"변경 성공"));
    }

    @Operation(summary = "이메일 변경",description = "회원은 변경할 이메일에 대해 이메일 인증을 진행한 후 성공하면 이메일 변경이 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"변경 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"인증번호 및 이메일이 잘못되었습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근 이거나 일반 사용자만 변경 가능합니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\" 올바르지 않은 접근입니다.\"}")))
    })
    @PutMapping("/user/info/update/email")
    public ResponseEntity<?> userInfoEmailUpdate(@RequestBody UserInfoEmailRequest userInfoEmailRequest,
                                                 @Parameter(hidden = true) @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                                 @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        validCookie(cookieLoginId, principalDetails);

        memberService.updateEmail(userInfoEmailRequest.getEmail(),userInfoEmailRequest.getCode(),principalDetails.getMemberId());

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"변경 성공"));
    }

    @Operation(summary = "회원 탈퇴",description = "일반 사용자가 해당 사이트를 탈퇴합니다. 간단한 동의를 체크한 경우에만 회원 탈퇴가 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"탈퇴 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"약관 동의를 해주세요\"}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"일반 사용자만 가능합니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 접근입니다.\"}")))
    })
    @DeleteMapping("/user/info/disconnect")
    public ResponseEntity<?> deleteMember(@RequestBody UserDeleteIdRequest userDeleteIdRequest,
                                          @Parameter(hidden = true) @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                          @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        validCookie(cookieLoginId,principalDetails);
        memberService.deleteMember(principalDetails.getMemberId(), userDeleteIdRequest.isCheckBox());

        ResponseCookie loginId = cookieUtils.deleteCookie("login-id");
        ResponseCookie refreshToken = cookieUtils.deleteCookie("RefreshToken");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,loginId.toString())
                .header(HttpHeaders.SET_COOKIE,refreshToken.toString())
                .body(new ControllerApiResponse<>(true,"탈퇴 성공"));
    }
    private void validCookie(String cookieLoginId, PrincipalDetails principalDetails) {
        cookieUtils.validCookie(cookieLoginId, principalDetails.getName());
    }
}


