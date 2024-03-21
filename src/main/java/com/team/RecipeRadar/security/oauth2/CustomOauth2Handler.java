package com.team.RecipeRadar.security.oauth2;


import com.team.RecipeRadar.global.jwt.JwtProvider;
import com.team.RecipeRadar.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    //소셜 로그인 성공시 해당로직을 타게되며 accessToken 과 RefreshToken을 발급해준다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess실행");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String loginId = principal.getMember().getLoginId();

        String jwtToken = jwtProvider.generateAccessToken(loginId);
        String refreshToken = jwtProvider.generateRefreshToken(loginId);

        response.addHeader("Authorization","Bearer "+ jwtToken);
        response.addHeader("RefreshToken","Bearer "+ refreshToken);

        String targetUrl = "/api/auth/success";
        RequestDispatcher dis = request.getRequestDispatcher(targetUrl);
        dis.forward(request, response);
    }
}
