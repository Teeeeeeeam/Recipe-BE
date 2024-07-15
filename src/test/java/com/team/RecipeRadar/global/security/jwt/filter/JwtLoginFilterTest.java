package com.team.RecipeRadar.global.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.utils.CookieUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.FilterChain;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtLoginFilterTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtProvider jwtProvider;
    @Mock private CookieUtils cookieUtils;

    @InjectMocks private JwtLoginFilter jwtLoginFilter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void attemptAuthentication_success() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        member.setLoginId("testUser");
        member.setPassword("testPassword");
        request.setContent(objectMapper.writeValueAsBytes(member));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken("testUser", "testPassword");
        when(authenticationManager.authenticate(any())).thenReturn(authToken);

        Authentication result = jwtLoginFilter.attemptAuthentication(request, response);

        assertThat(authToken).isEqualTo(result);
    }

    @Test
    @DisplayName("인증 성공 테스트")
    void successfulAuthentication() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Member member = new Member();
        member.setLoginId("testUser");
        member.setPassword("testPassword");

        PrincipalDetails principalDetails = new PrincipalDetails(member);

        Authentication authResult = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        Map<String, String> tokenMap = Map.of("accessToken", "accessTokenValue", "refreshToken", "refreshTokenValue");
        when(jwtProvider.generateToken(any())).thenReturn(tokenMap);

        ResponseCookie responseCookie = ResponseCookie.from("RefreshToken", "token").maxAge(100 * 36).build();
        when(cookieUtils.createCookie(anyString(), anyString(), anyInt())).thenReturn(responseCookie);

        jwtLoginFilter.successfulAuthentication(request, response, chain, authResult);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        ControllerApiResponse<String> expectedResponse = new ControllerApiResponse<>(true, "로그인 성공", "accessTokenValue");
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);
        assertThat(response.getContentAsString()).isEqualTo(expectedResponseBody);
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void attemptAuthentication_failure() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        member.setLoginId("testUser");
        member.setPassword("testPassword");
        request.setContent(objectMapper.writeValueAsBytes(member));

        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Invalid credentials") {});

        assertThatThrownBy(() -> jwtLoginFilter.attemptAuthentication(request,response)).isInstanceOf(JwtTokenException.class);
    }

    @Test
    @DisplayName("인증 실패 테스트")
    void unsuccessfulAuthentication() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException exception = new AuthenticationException("Invalid credentials") {};

        assertThatThrownBy(() -> jwtLoginFilter.unsuccessfulAuthentication(request,response,exception)).isInstanceOf(JwtTokenException.class);
    }
}