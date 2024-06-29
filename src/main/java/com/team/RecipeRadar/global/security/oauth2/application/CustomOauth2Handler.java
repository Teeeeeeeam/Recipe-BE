package com.team.RecipeRadar.global.security.oauth2.application;

import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import lombok.RequiredArgsConstructor;
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
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CustomOauth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final CookieUtils cookieUtils;

    private final String ACCESS_TOKEN = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";

    @Value("${host.path}")
    private String successUrl;


    //소셜 로그인 성공시 해당로직을 타게되며 accessToken 과 RefreshToken을 발급해준다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Map<String, String> toekenMap = jwtProvider.generateToken(principal.getMemberDto(principal.getMember()));

        String accessToken = toekenMap.get(ACCESS_TOKEN);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(successUrl);

        String redirectURI = builder
                .queryParam("access-token", accessToken)
                .build().toString();

        ResponseCookie responseCookie = cookieUtils.createCookie("RefreshToken", toekenMap.get(REFRESH_TOKEN), 30 * 24 * 60 * 60);

        if (accessToken != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.sendRedirect(redirectURI);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "페이지를 찾을 수 없습니다.");
        }
    }
}
