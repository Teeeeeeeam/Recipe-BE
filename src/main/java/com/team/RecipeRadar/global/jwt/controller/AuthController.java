package com.team.RecipeRadar.global.jwt.controller;

import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.Service.JwtAuthService;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "로그인 관련 컨트롤러", description = "로그인시 관련된 API")
@Slf4j
@RequestMapping("/api")
public class AuthController {

    private final JwtAuthService  jwtAuthService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "소셜 로그인시 성공시 리다이렉트", description = "소셜 로그인을 성공하면 해당 앤드포인트로 리다이렉트됩니다. 응답 헤더에 Authorization과 Refreshtoken이 존재")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"JWT 발행 성공\"}")))
    })
    @GetMapping("/auth/success")
    public ResponseEntity<?> signinSuccess() {;
        return ResponseEntity.ok(new ControllerApiResponse(true, "JWT 발행 성공"));
    }

    // AcessToken만료시 RefreshToken을 검증하는 url
    @Operation(summary = "AccessToken만료시 요청 API", description = "기존의 AccessToken만료 되었을때 해당 엔드포인트로 RefreshToken을 요청애 AccessToken을 재발급 , 헤더에 AccessToken 발급" +
            "(RefrehToken을 보낼떄는  리프레쉬토큰앞에 'Bearer ' 을작성하여 보내야함 )")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"새로운 accessToken 발급\"}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500" ,description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/refreshToken/valid")
    public ResponseEntity<?> RefreshToke(HttpServletRequest request, HttpServletResponse response){
        try {
            String refreshToken = request.getHeader("RefreshToken");
            log.info("aaasda={}",refreshToken);
            String substring = refreshToken.substring(refreshToken.indexOf("Bearer ") + 7);
            log.info("sb={}",substring);
            String token = jwtProvider.validateRefreshToken(substring);
            log.info("token={}",token);

            if (token!=null){
                response.addHeader("Authorization","Bearer "+ token);
                return ResponseEntity.ok(new ControllerApiResponse(true,"새로운 accessToken 발급"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ControllerApiResponse(false,"토큰이 만료되었거나 일치하지않습니다."));
        }catch (JwtTokenException e){
            throw new JwtTokenException(e.getMessage());
        }
    }

    @Operation(summary = "로그인", description = "아이디, 비밀번호를 입력하여 로그인을 하는 API 로그인 성공시 Header에 JWT토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":{\"accessToken\":\"[AccessToken]\",\"refreshToken\":\"[RefreshToken]\"}}"))),
            @ApiResponse(responseCode = "400" ,description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\":false,\"message\":{\"password\":\"비밀번호를 입력해주세요\",\"loginId\":\"아이디를 입력해주세요\"}}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"로그인 실패\"}"))),
            @ApiResponse(responseCode = "500" ,description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/singin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, BindingResult result){
        try {
            if (result.hasErrors()){
                Map<String, String> errorMap = new HashMap<>();
                for (FieldError error : result.getFieldErrors()){
                    errorMap.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ControllerApiResponse(false,errorMap));
            }
            Map<String, String> login = jwtAuthService.login(loginDto);

            return ResponseEntity.ok(new ControllerApiResponse(true, login));
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

    @Operation(summary = "로그아웃", description = "해당 사용자의 id를 보내서 로그아웃(RefreshToken과 AccessToken을 삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"로그아웃 성공\"}"))),
            @ApiResponse(responseCode = "401" ,description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"해당 회원은 이미 로그아웃 했습니다.\"}"))),
            @ApiResponse(responseCode = "500" ,description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<?> LogOut(@RequestParam("memberid")String memberId) {
        try {
            long longId = Long.parseLong(memberId);
            jwtAuthService.logout(longId);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(new ControllerApiResponse(true, "로그아웃 성공"));
        } catch (Exception e) {
            throw new JwtTokenException(e.getMessage());
        }
    }

}
