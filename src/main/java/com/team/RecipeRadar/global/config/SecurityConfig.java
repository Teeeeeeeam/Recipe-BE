package com.team.RecipeRadar.global.config;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import com.team.RecipeRadar.global.security.exception.CustomAccessDeniedHandler;
import com.team.RecipeRadar.global.security.exception.JwtAuthenticationEntryPoint;
import com.team.RecipeRadar.global.security.jwt.filter.*;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Service;
import com.team.RecipeRadar.global.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final CustomOauth2Handler customOauth2Handler;
    private final CustomOauth2Service customOauth2Service;
    private final CookieUtils cookieUtils;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).disable();

        http
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler);

        http
                .addFilterBefore(new SecurityExceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(),memberRepository,jwtProvider), JwtLoginFilter.class)
                .addFilterBefore(new CustomLogoutFilter(refreshTokenRepository,jwtProvider,cookieUtils), LogoutFilter.class)
                .addFilterAt(new JwtLoginFilter(authenticationManager(),jwtProvider,cookieUtils), UsernamePasswordAuthenticationFilter.class);
        http
                .authorizeRequests()
                .mvcMatchers("/api/user/**").access("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .oauth2Login().loginPage("/api/login").successHandler(customOauth2Handler).userInfoEndpoint().userService(customOauth2Service);
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("*"));        //모든 요청의 허용
        config.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
