package com.team.RecipeRadar.global.jwt.controller;

import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.Service.JwtAuthService;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Slf4j
@RequestMapping("/api")
public class AuthController {

    private final JwtAuthService  jwtAuthService;
    private final JwtProvider jwtProvider;

    //로그인시 발급될 리다이렉트 URL
    @GetMapping("/auth/success")
    public ResponseEntity<?> signinSuccess() {;
        return ResponseEntity.ok(new ControllerApiResponse(true, "JWT 발행 성공"));
    }

    // AcessToken만료시 RefreshToken을 검증하는 url
    @PostMapping("/auth/refreshToken/valid")
    public ResponseEntity<?> RefreshToke(HttpServletRequest request, HttpServletResponse response){
        try {
            String refreshToken = request.getHeader("RefreshToken");
            String substring = refreshToken.substring(refreshToken.indexOf("Bearer ") + 7);
            String token = jwtProvider.validateRefreshToken(substring);

            if (token!=null){
                response.addHeader("Authorization","Bearer "+ token);
                return ResponseEntity.ok(new ControllerApiResponse(true,"새로운 accessToken 발급"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ControllerApiResponse(false,"토큰이 만료되었거나 일치하지않습니다."));
        }catch (JwtTokenException e){
            throw new JwtTokenException(e.getMessage());
        }
    }

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
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException(e.getMessage());
        }
    }

    // 로그아웃 url
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
