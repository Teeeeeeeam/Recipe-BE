package com.team.RecipeRadar.domain.userInfo.api;

import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoEmailRequest;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoUpdateNickNameRequest;
import com.team.RecipeRadar.domain.userInfo.application.UserInfoService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "사용자 페이지 컨트롤러",description = "사용자 페이지 API")
@RequestMapping("/api")
public class UserInfoController {


    private final UserInfoService userInfoService;

    @Operation(summary = "회원정보 조회", description = "회원의 회원정보(이름,닉네임,이메일,로그인타입)을 조회 한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"조회성공\", \"data\": {\"username\": \"홍길동\", \"nickName\":\"홍길동\", \"email\":\"test@naver.com\",\"loginType\":\"normal\" }}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/info/{login-id}")
    public ResponseEntity<?> userInfo(@PathVariable("login-id")String loginId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            UserInfoResponse members = userInfoService.getMembers(loginId,name);

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
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/user/info/update/nickname")
    public ResponseEntity<?> userInfoNickNameUpdate(@RequestBody UserInfoUpdateNickNameRequest userInfoUpdateNickNameRequest){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticationName = authentication.getName();

            String nickName = userInfoUpdateNickNameRequest.getNickName();
            String loginId = userInfoUpdateNickNameRequest.getLoginId();

            userInfoService.updateNickName(nickName,loginId,authenticationName);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"변경 성공"));
        }catch (AccessDeniedException e){
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
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/user/info/update/email")
    public ResponseEntity<?> userInfoEmailUpdate(@RequestBody UserInfoEmailRequest userInfoEmailRequest){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticationName = authentication.getName();

            userInfoService.updateEmail(userInfoEmailRequest.getEmail(),userInfoEmailRequest.getCode(),userInfoEmailRequest.getLoginId(),authenticationName);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"변경 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }catch (ServerErrorException e){
            throw new ServerErrorException(e.getMessage());
        }
    }

}
