package com.team.RecipeRadar.domain.userInfo.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.userInfo.dto.info.*;
import com.team.RecipeRadar.domain.userInfo.application.UserInfoService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.security.oauth2.UserDisConnectService;
import com.team.RecipeRadar.global.security.oauth2.provider.Oauth2UrlProvider;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자 페이지 컨트롤러",description = "사용자 페이지 API(해당 페이지 접근시 /api/user/info/valid 를 통해 쿠키값을 받고난후에 접근가능 쿠키의 만료시간은 20분으로 설정)")
@RequestMapping("/api")
public class UserInfoController {


    private final UserInfoService userInfoService;

    @Qualifier("kakao")
    private final UserDisConnectService kakaoDisConnectService;
    @Qualifier("naver")
    private final UserDisConnectService naverDisConnectService;
    private final Oauth2UrlProvider urlProvider;

    @Operation(summary = "회원정보 조회", description = "회원의 회원정보(이름,닉네임,이메일,로그인타입)을 조회 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"조회성공\", \"data\": {\"username\": \"홍길동\", \"nickName\":\"홍길동\", \"email\":\"test@naver.com\",\"loginType\":\"normal\" }}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/info/{login-id}")
    public ResponseEntity<?> userInfo(@PathVariable("login-id")String loginId,@CookieValue(name = "login-id",required = false) String cookieLoginId){
        try{
            MemberDto memberDto = getMemberDto();
            cookieValid(cookieLoginId,memberDto.getLoginId());

            UserInfoResponse members = userInfoService.getMembers(loginId,memberDto.getUsername());

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회성공",members));
        }catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }catch (ServerErrorException e){
            throw new ServerErrorException(e.getMessage());
        }
    }



    @Operation(summary = "회원의 닉네임을 수정", description = "회원의 닉네임을 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"변경 성공\"}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/user/info/update/nickname")
    public ResponseEntity<?> userInfoNickNameUpdate(@RequestBody UserInfoUpdateNickNameRequest userInfoUpdateNickNameRequest,@CookieValue(name = "login-id",required = false) String cookieLoginId){
        try {
            MemberDto memberDto = getMemberDto();
            cookieValid(cookieLoginId,memberDto.getLoginId());
            String nickName = userInfoUpdateNickNameRequest.getNickName();
            String loginId = userInfoUpdateNickNameRequest.getLoginId();

            userInfoService.updateNickName(nickName,loginId,memberDto.getUsername());

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"변경 성공"));
        }catch (ForbiddenException e){
            throw new ForbiddenException(e.getMessage());
        } catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }catch (ServerErrorException e){
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Operation(summary = "회원 이메일 수정", description = "회원은 변경할 이메일의 대해 이메일인증을 진행후 성공하면 이메일 변경을 성공. (이메일전송및 인증코드 검증은 '/api/search/email-**' API를 먼저 사용해주세요 추후에 엔트포인트 명을 변경예정)")
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
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/user/info/update/email")
    public ResponseEntity<?> userInfoEmailUpdate(@RequestBody UserInfoEmailRequest userInfoEmailRequest,@CookieValue(name = "login-id",required = false) String cookieLoginId){
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

    @Operation(summary = "사용자 페이지 접근시 비밀번호 검증", description = "사용자 페이지에서 기능을 사용하기 위해서는 해당 API에서 비밀번호를 통해 사용자 임을 인증을한다. 성공시에는 20분동안 유효한 쿠키를 만들어 발급후 사용자 페이지에서 사용가능, 만약 쿠키가 발급되지않은" +
            "상태에서 사용자 페이지를 URL로 직접 접근시에는 403 Forbiden 에러발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"인증 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"비밀번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/user/info/valid")
    public ResponseEntity<?> userInfoValid(@RequestBody UserValidRequest passwordDTO){
        try {
            MemberDto memberDto = getMemberDto();

            String userToken=userInfoService.userToken(memberDto.getLoginId(), memberDto.getUsername(), passwordDTO.getPassword(), passwordDTO.getLoginType());
            String userEncodeToken = new String(Base64.getEncoder().encode(userToken.getBytes()));

            ResponseCookie responseCookie = ResponseCookie.from("login-id", userEncodeToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(1200)
                    .build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(new ControllerApiResponse<>(true, "인증 성공"));

        } catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (ServerErrorException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }


    @Operation(summary = "일반 사용자 회원 탈퇴", description = "일반 사용자가 해당 사이트를 탈퇴한다. 간단한 동의하는 버튼을 만들어 체크를 할경우에 회원 탈퇴 가능")
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
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"올바르지 않은 쿠키값으로 접근\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
                    .location(URI.create("http://localhost:3000/success/status?unlink="+disconnected)).build();
    }

    @RequestMapping(value = "/oauth2/unlink/naver",method = {RequestMethod.GET, RequestMethod.POST})
    @Hidden
    public ResponseEntity<?> naverUnlink(@RequestParam("code")String auth2Code){
        String accessToken = naverDisConnectService.getAccessToken(auth2Code);
        Boolean disconnected = naverDisConnectService.disconnect(accessToken);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/success/status?unlink="+disconnected)).build();
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
}
