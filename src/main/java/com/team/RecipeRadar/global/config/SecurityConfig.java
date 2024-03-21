package com.team.RecipeRadar.global.config;

import com.team.RecipeRadar.global.jwt.ExceptionHandlerFilter;
import com.team.RecipeRadar.global.jwt.JwtLoginFilter;
import com.team.RecipeRadar.global.jwt.JwtAuthorizationFilter;
import com.team.RecipeRadar.global.jwt.JwtProvider;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Service;
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .addFilterBefore(new ExceptionHandlerFilter(),UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .formLogin().disable()

                .httpBasic().disable();


        http
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(),memberRepository,jwtProvider), JwtLoginFilter.class)
                .addFilterAt(new JwtLoginFilter(authenticationManager(),jwtProvider), UsernamePasswordAuthenticationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/api/user/**").access("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .oauth2Login().loginPage("/login").successHandler(customOauth2Handler).userInfoEndpoint().userService(customOauth2Service);

        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).disable();
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
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
