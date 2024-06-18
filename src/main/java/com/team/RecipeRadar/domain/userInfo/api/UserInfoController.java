package com.team.RecipeRadar.domain.userInfo.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.userInfo.dto.info.*;
import com.team.RecipeRadar.domain.userInfo.application.UserInfoService;
import com.team.RecipeRadar.domain.userInfo.utils.CookieUtils;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.security.oauth2.UserDisConnectService;
import com.team.RecipeRadar.global.security.oauth2.provider.Oauth2UrlProvider;
import io.swagger.v3.oas.annotations.Hidden;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Slf4j
@Tag(name = "사용자 - 마이페이지 컨트롤러",description = "사용자 페이지 API 사용자의 정보 및 활동 기록을 확인할 수 있습니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserInfoController {


    private final UserInfoService userInfoService;
    private final CookieUtils cookieUtils;

    @Value("${disconnect.oauth2.redirect}")
    private String redirectUrl;
    @Qualifier("kakao")
    private final UserDisConnectService kakaoDisConnectService;
    @Qualifier("naver")
    private final UserDisConnectService naverDisConnectService;
    private final Oauth2UrlProvider urlProvider;

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

        UserInfoResponse members = userInfoService.getMembers(principalDetails.getMemberId());

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
        userInfoService.updateNickName(userInfoUpdateNickNameRequest.getNickName(), principalDetails.getMemberId());
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
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}")))
    })
    @PutMapping("/user/info/update/email")
    public ResponseEntity<?> userInfoEmailUpdate(@RequestBody UserInfoEmailRequest userInfoEmailRequest,
                                                 @CookieValue(name = "login-id",required = false) String cookieLoginId){
        try {
            MemberDto memberDto = getMemberDto();
            cookieValid(cookieLoginId,memberDto.getLoginId());

            userInfoService.updateEmail(userInfoEmailRequest.getEmail(),userInfoEmailRequest.getCode(),userInfoEmailRequest.getLoginId(),memberDto.getUsername(),userInfoEmailRequest.getLoginType());

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"변경 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }catch (ServerErrorException e){
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Operation(summary = "사용자 페이지 쿠키 발급",
            description = "사용자 페이지에서 기능을 사용하기 위해 해당 API에서 비밀번호를 통해 사용자를 인증합니다. 인증에 성공하면 20분 동안 유효한 쿠키를 발급하여 사용자 페이지에서 사용할 수 있습니다. 만약 쿠키가 발급되지 않은 상태에서 사용자 페이지를 URL로 직접 접근하면 403 Forbidden 에러가 발생합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"인증 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"비밀번호가 일치하지 않습니다.\"}")))
    })
    @PostMapping("/user/info/valid")
    public ResponseEntity<?> userInfoValid(@RequestBody UserValidRequest passwordRequest){
        try {
            MemberDto memberDto = getMemberDto();

            String userToken=userInfoService.userToken(memberDto.getLoginId(), memberDto.getUsername(), passwordRequest.getPassword(), passwordRequest.getLoginType());
            ResponseCookie userInfoCookie = cookieUtils.createCookie("login-id", userToken, 1200);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, userInfoCookie.toString()).body(new ControllerApiResponse<>(true, "인증 성공"));

        } catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (ServerErrorException e) {
            throw new ServerErrorException(e.getMessage());
        }
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
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근 이거나 일반 사용자만 가능합니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 쿠키값으로 접근\"}")))
    })
    @DeleteMapping("/user/info/disconnect")
    public ResponseEntity<?> deleteMember(@RequestBody UserDeleteIdRequest userDeleteIdRequest,@CookieValue(name = "login-id",required = false) String cookieLoginId){
        try {
            MemberDto memberDto = getMemberDto();
            cookieValid(cookieLoginId,memberDto.getLoginId());
            userInfoService.deleteMember(userDeleteIdRequest.getLoginId(),userDeleteIdRequest.isCheckBox(),memberDto.getUsername());
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"탈퇴 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (ForbiddenException e){
            throw new ForbiddenException(e.getMessage());
        } catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException(e.getMessage());
        }

    }

    @Operation(summary = "즐겨찾기 내역(페이징)", description = "사용자가 즐겨찾기한 레시피에 대해 무한 페이징을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"hasNext\":true,\"bookmark_list\":[{\"id\":128671,\"title\":\"어묵김말이\"}]}}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"쿠키값이 없을때 접근\"}")))
    })
    @GetMapping("/user/info/bookmark")
    public ResponseEntity<?> userInfoBookmark(@RequestParam(value = "last-id",required = false)Long lastId,
                                              @CookieValue(name = "login-id",required = false) String cookieLoginId,
                                              @Parameter(example = "{\"size\":10}")Pageable pageable){
        try {
            if (cookieLoginId == null) {
                throw new ForbiddenException("쿠키값이 없을때 접근");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

            Long member_id = principal.getMemberDto(principal.getMember()).getId();

            UserInfoBookmarkResponse userInfoBookmarkResponse = userInfoService.userInfoBookmark(member_id, lastId, pageable);

            return ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공", userInfoBookmarkResponse));
        }catch (ForbiddenException e){
            throw new ForbiddenException(e.getMessage());
        } catch (Exception e){
            throw new ServerErrorException("서버오류");
        }
    }
    @GetMapping ("/oauth2/social/unlink")
    @Hidden
    public void socialUnlink(@RequestParam(value = "social-id") String loginType, HttpServletResponse response) throws IOException {
        String redirectUrl = urlProvider.getRedirectUrl(loginType);
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping(value = "/oauth2/unlink/kakao",method = {RequestMethod.GET, RequestMethod.POST})
    @Hidden
    public ResponseEntity<?> kakaoUnlink(@RequestParam("code")String auth2Code){
        String accessToken = kakaoDisConnectService.getAccessToken(auth2Code);
        Boolean disconnected = kakaoDisConnectService.disconnect(accessToken);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl+disconnected)).build();
    }

    @RequestMapping(value = "/oauth2/unlink/naver",method = {RequestMethod.GET, RequestMethod.POST})
    @Hidden
    public ResponseEntity<?> naverUnlink(@RequestParam("code")String auth2Code){
        String accessToken = naverDisConnectService.getAccessToken(auth2Code);
        Boolean disconnected = naverDisConnectService.disconnect(accessToken);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl+disconnected)).build();
    }

    private void cookieValid(String cookieLoginId,String loginId ) {

        boolean validUserToken = userInfoService.validUserToken(cookieLoginId, loginId);
        if (cookieLoginId ==null||!validUserToken){
            throw new ForbiddenException("올바르지 않은 쿠키값으로 접근");
        }

    }

    private static String getAuthenticationName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticationName = authentication.getName();
        log.info("au={}",authentication);
        return authenticationName;
    }

    private static MemberDto getMemberDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        MemberDto memberDto = principal.getMemberDto(principal.getMember());
        log.info("memberDot={}",memberDto);
        return memberDto;
    }

    private void validCookie(String cookieLoginId, PrincipalDetails principalDetails) {
        cookieUtils.validCookie(cookieLoginId, principalDetails.getName());
    }
}
