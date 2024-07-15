package com.team.RecipeRadar.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CookieUtils cookieUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String URL ="/api/login";
    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, CookieUtils cookieUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.cookieUtils = cookieUtils;
        setFilterProcessesUrl(URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            Member member = objectMapper.readValue(request.getInputStream(), Member.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(member.getLoginId(), member.getPassword());
            return authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new JwtTokenException("로그인 실패");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();
        Map<String, String> tokenMap = jwtProvider.generateToken(principal.getMemberDto(principal.getMember()));

        String accessToken = tokenMap.get("accessToken");
        String refreshToken = tokenMap.get("refreshToken");

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ResponseCookie refreshTokenCookie = cookieUtils.createCookie("RefreshToken", refreshToken, 30 * 24 * 60 * 60);
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        ControllerApiResponse<String> apiResponse = new ControllerApiResponse<>(true, "로그인 성공", accessToken);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        throw new JwtTokenException("로그인 실패");
    }
}
