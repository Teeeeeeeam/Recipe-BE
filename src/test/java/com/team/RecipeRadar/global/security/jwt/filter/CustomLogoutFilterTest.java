package com.team.RecipeRadar.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class CustomLogoutFilterTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CookieUtils cookieUtils;

    @InjectMocks
    private CustomLogoutFilter customLogoutFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void doFilter_logoutSuccess() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/logout");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        Cookie refreshTokenCookie = new Cookie("RefreshToken", "valid_refresh_token");
        request.setCookies(refreshTokenCookie);

        when(jwtProvider.validateRefreshToken(anyString())).thenReturn(null);

        doNothing().when(refreshTokenRepository).deleteByRefreshToken(anyString());

        ResponseCookie deleteCookie = ResponseCookie.from("RefreshToken", "").maxAge(0).build();
        when(cookieUtils.deleteCookie(anyString())).thenReturn(deleteCookie);

        customLogoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE+ ";charset=UTF-8");

        ControllerApiResponse expectedApiResponse = new ControllerApiResponse(true, "로그아웃 성공");
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedApiResponse));

        verify(refreshTokenRepository, times(1)).deleteByRefreshToken("valid_refresh_token");
    }

    @Test
    @DisplayName("쿠키 누락으로 인한 로그아웃 실패 테스트")
    void doFilter_missingCookie() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/logout");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        customLogoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE+ ";charset=UTF-8");

        ControllerApiResponse expectedApiResponse = new ControllerApiResponse(false, "쿠키가 존재하지 않습니다.");
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedApiResponse));;
    }

    @Test
    @DisplayName("잘못된 토큰으로 인한 로그아웃 실패 테스트")
    void doFilter_invalidToken() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/logout");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        Cookie refreshTokenCookie = new Cookie("RefreshToken", "invalid_refresh_token");
        request.setCookies(refreshTokenCookie);

        doThrow(new JwtTokenException("토큰이 잘못되었습니다.")).when(jwtProvider).validateRefreshToken("invalid_refresh_token");

        customLogoutFilter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE+ ";charset=UTF-8");

        ControllerApiResponse expectedApiResponse = new ControllerApiResponse(false, "토큰이 잘못되었습니다.");
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedApiResponse));

    }
}