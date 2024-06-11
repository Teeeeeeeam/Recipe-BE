package com.team.RecipeRadar.global.jwt.controller;

import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.Service.JwtAuthService;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.jwt.dto.MemberInfoResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
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
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "공용 - 로그인 컨트롤러", description = "로그인 및 토큰 관리")
@Slf4j
@RequestMapping("/api")
public class AuthController {

    private final JwtAuthService  jwtAuthService;
    private final JwtProvider jwtProvider;

    // AcessToken만료시 RefreshToken을 검증하는 url

    @Operation(summary = "엑세스토큰 재발급", description = "엑세스 토큰이 만료되면 리프레시 토큰을 사용하여 새로운 엑세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"새로운 accessToken 발급\"}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"토큰이 만료되었거나 일치하지않습니다.\"}"))),
    })
    @PostMapping("/auth/refresh-token/validate")
    public ResponseEntity<?> RefreshToken(HttpServletRequest request){
        try {
            String refreshToken = "";
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("RefreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken!=""){
                if(jwtProvider.TokenExpiration(refreshToken)){
                    ResponseCookie deleteCookie = ResponseCookie.from("RefreshToken", null).maxAge(0).path("/").build();
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.SET_COOKIE,deleteCookie.toString()).body(new ErrorResponse<>(false,"토큰이 만료되었거나 일치하지않습니다."));   // 만료시 삭제
                }
                String accessToken = jwtProvider.validateRefreshToken(refreshToken);
                return ResponseEntity.ok(new ControllerApiResponse(true,"새로운 accessToken 발급",accessToken));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse<>(false,"토큰이 만료되었거나 일치하지않습니다."));
        }catch (JwtTokenException e){
            throw new JwtTokenException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호를 입력하여 로그인하는 API. 로그인이 성공하면 JWT 토큰과 쿠키의 RefreshToken이 발급됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\" :\"로그인성공\", \"data\": {\"accessToken\":\"[AccessToken]\"}}"))),
            @ApiResponse(responseCode = "400" ,description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\":false,\"message\":\"실패\",\"data\":{\"password\":\"비밀번호를 입력해주세요\",\"loginId\":\"아이디를 입력해주세요\"}}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"로그인 실패\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result){
        try {
            if (result.hasErrors()){
                Map<String, String> errorMap = new HashMap<>();
                for (FieldError error : result.getFieldErrors()){
                    errorMap.put(error.getField(),error.getDefaultMessage());
                }

                ControllerApiResponse<Object> failResponse = ControllerApiResponse.builder()
                        .success(false)
                        .message("실패")
                        .data(errorMap).build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failResponse);
            }
            Map<String, String> login = jwtAuthService.login(loginRequest);

            String refreshToken = login.get("refreshToken");

            ResponseCookie responseCookie = ResponseCookie.from("RefreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(30 * 24 * 60 * 60)
                    .build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(new ControllerApiResponse<>(true,"로그인 성공",Map.of("accessToken",login.get("accessToken"))));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException(e.getMessage());
        }
    }
    @Operation(summary = "엑세스 토큰 정보 조회", description = "로그인시 획득한 AccessToken에 대한 사용자 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\" :\"조회 성공\", \"data\": {\"id\":\"member_id\",\"loginId\":\"로그인 아이디\",\"nickName\":\"닉네임\",\"loginType\":\"normal\"}}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"토큰이 존재하지 않습니다.\"}")))
    })
    @PostMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request){
        try{
            String accessToken = request.getHeader("Authorization");
            if(accessToken ==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse<>(false,"토큰이 존재하지 않습니다."));
            }
            String token = accessToken.substring(accessToken.indexOf("Bearer ") + 7);
            MemberInfoResponse info = jwtAuthService.accessTokenMemberInfo(token);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",info));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Operation(summary = "로그아웃", description = "해당 사용자의 ID를 보내 로그아웃.(RefreshToken과 AccessToken 모두 삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"로그아웃 성공\"}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"해당 회원은 이미 로그아웃 했습니다.\"}")))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> LogOut(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(value = "{\"member-id\":\"사용자 member-id\"}")))
                                        @RequestBody Map<String,String> data) {
        try {
            long memberId = Long.parseLong(data.get("member-id"));
            jwtAuthService.logout(memberId);
            SecurityContextHolder.clearContext();

            ResponseCookie deleteCookie = ResponseCookie.from("RefreshToken", null)
                    .secure(true)
                    .httpOnly(true)
                    .sameSite("None")
                    .maxAge(0).path("/").build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,deleteCookie.toString()).body(new ControllerApiResponse<>(true,"로그아웃 성공"));
        } catch (Exception e) {
            throw new JwtTokenException(e.getMessage());
        }
    }

}
