package com.team.RecipeRadar.global.security.jwt.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class JwtAuthorizationFilterTest {

    @Mock MemberRepository memberRepository;
    @Mock JwtProvider jwtProvider;
    @Mock AuthenticationManager authenticationManager;
    @InjectMocks JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공 테스트")
    void doFilterInternal_validToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_jwt_token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        when(jwtProvider.TokenExpiration("valid_jwt_token")).thenReturn(false);
        when(jwtProvider.validateAccessToken("valid_jwt_token")).thenReturn("testUser");

        Member member = new Member();
        member.setLoginId("testUser");
        when(memberRepository.findByLoginId("testUser")).thenReturn(member);

        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);

        verify(filterChain).doFilter(request, response);
    }


    @Test
    @DisplayName("만료된 JWT 토큰으로 인증 실패 테스트")
    void doFilterInternal_expiredToken(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expired_jwt_token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtProvider.TokenExpiration("expired_jwt_token")).thenReturn(true);

        assertThatThrownBy(() -> jwtAuthorizationFilter.doFilterInternal(request,response,filterChain)).isInstanceOf(JwtTokenException.class);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 인증 실패 테스트")
    void doFilterInternal_invalidToken(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_jwt_token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtProvider.TokenExpiration("invalid_jwt_token")).thenThrow(new JWTDecodeException("Invalid token"));

        assertThatThrownBy(() -> jwtAuthorizationFilter.doFilterInternal(request,response,filterChain)).isInstanceOf(JwtTokenException.class);
    }
}