package com.team.RecipeRadar.global.conig;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import com.team.RecipeRadar.global.security.exception.CustomAccessDeniedHandler;
import com.team.RecipeRadar.global.security.exception.JwtAuthenticationEntryPoint;
import com.team.RecipeRadar.global.security.jwt.filter.SecurityExceptionHandlerFilter;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Service;
import com.team.RecipeRadar.global.utils.CookieUtils;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({JwtAuthenticationEntryPoint.class,CustomAccessDeniedHandler.class})
public class TestConfig {

    @Bean
    public MemberRepository memberRepository() {
        return Mockito.mock(MemberRepository.class);
    }

    @Bean
    public JwtProvider jwtProvider() {
        return Mockito.mock(JwtProvider.class);
    }

    @Bean
    public CustomOauth2Handler customOauth2Handler() {
        return Mockito.mock(CustomOauth2Handler.class);
    }

    @Bean
    public CustomOauth2Service customOauth2Service() {
        return Mockito.mock(CustomOauth2Service.class);
    }

    @Bean
    public CookieUtils cookieUtils() {
        return Mockito.mock(CookieUtils.class);
    }

    @Bean
    public RefreshTokenRepository refreshTokenRepository() {
        return Mockito.mock(RefreshTokenRepository.class);
    }

//    @Bean
//    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
//        return Mockito.mock(JwtAuthenticationEntryPoint.class);
//    }
//
//    @Bean
//    public CustomAccessDeniedHandler customAccessDeniedHandler() {
//        return Mockito.mock(CustomAccessDeniedHandler.class);
//    }

    @Bean
    public SecurityExceptionHandlerFilter securityExceptionHandlerFilter() {
        return new SecurityExceptionHandlerFilter();
    }
}
