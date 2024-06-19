package com.team.RecipeRadar.global.security.oauth2;


import com.team.RecipeRadar.domain.userInfo.utils.CookieUtils;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final CookieUtils cookieUtils;


    @Value("${host.path}")
    private String successUrl;

    //소셜 로그인 성공시 해당로직을 타게되며 accessToken 과 RefreshToken을 발급해준다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String loginId = principal.getMember().getLoginId();

        String jwtToken = jwtProvider.generateAccessToken(loginId);
        String refreshToken = jwtProvider.generateRefreshToken(loginId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(successUrl);

        String redirectURI = builder
                .queryParam("access-token", jwtToken)
                .build().toString();

        ResponseCookie responseCookie = cookieUtils.createCookie("RefreshToken", refreshToken, 30 * 24 * 60 * 60);

        if (jwtToken != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.sendRedirect(redirectURI);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "페이지를 찾을 수 없습니다.");
        }
    }
}
