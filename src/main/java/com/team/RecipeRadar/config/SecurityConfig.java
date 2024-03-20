package com.team.RecipeRadar.config;

import com.team.RecipeRadar.filter.jwt.ExceptionHandlerFilter;
import com.team.RecipeRadar.filter.jwt.JwtLoginFilter;
import com.team.RecipeRadar.filter.jwt.JwtAuthorizationFilter;
import com.team.RecipeRadar.filter.jwt.JwtProvider;
import com.team.RecipeRadar.repository.MemberRepository;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Handler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsConfig corsConfig;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(new ExceptionHandlerFilter(),UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable();


        http
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager(),memberRepository,jwtProvider), JwtLoginFilter.class)
                .addFilterAt(new JwtLoginFilter(authenticationManager(),jwtProvider), UsernamePasswordAuthenticationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/api/test/**").access("hasRole('ROLE_USER')or hasRole('ROLE_ADMIN')")
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

}