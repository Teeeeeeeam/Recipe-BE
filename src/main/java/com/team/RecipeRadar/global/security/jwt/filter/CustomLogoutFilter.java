package com.team.RecipeRadar.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private static final String LOGOUT_URI_PATTERN = "^\\/api/logout$";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";
    private static final String SUCCESS_LOGOUT_MESSAGE = "로그아웃 성공";
    private static final String COOKIE_NOT_FOUND_MESSAGE = "쿠키가 존재하지 않습니다.";
    private static final String INVALID_TOKEN_MESSAGE = "토큰이 잘못되었습니다.";

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final CookieUtils cookieUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        if (!requestURI.matches(LOGOUT_URI_PATTERN) || !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        handleLogout(request, response);
    }
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            responseErrorMessage(response, COOKIE_NOT_FOUND_MESSAGE);
            return;
        }

        Cookie refreshTokenCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .orElse(null);

        if (refreshTokenCookie == null) {
            responseErrorMessage(response, COOKIE_NOT_FOUND_MESSAGE);
            return;
        }

        String refreshToken = refreshTokenCookie.getValue();
        try {
            jwtProvider.validateRefreshToken(refreshToken);
        } catch (JwtTokenException e) {
            responseErrorMessage(response, INVALID_TOKEN_MESSAGE);
            return;
        }

        SecurityContextHolder.clearContext();
        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        ResponseCookie deleteCookie = cookieUtils.deleteCookie(REFRESH_TOKEN_COOKIE_NAME);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        response.setCharacterEncoding("UTF-8");

        sendSuccessResponse(response);
    }

    private void responseErrorMessage(HttpServletResponse response, String message) throws IOException {
        ControllerApiResponse apiResponse = new ControllerApiResponse(false, message);
        writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, apiResponse);
    }

    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        ControllerApiResponse apiResponse = new ControllerApiResponse(true, SUCCESS_LOGOUT_MESSAGE);
        writeJsonResponse(response, HttpServletResponse.SC_OK, apiResponse);
    }

    private void writeJsonResponse(HttpServletResponse response, int status, ControllerApiResponse apiResponse) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

}