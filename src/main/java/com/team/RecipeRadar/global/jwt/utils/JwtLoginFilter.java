package com.team.RecipeRadar.global.jwt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    /**
     * 로그인을 처리하는 Custom 메소드(Jwt 토큰을 사용해 로그인 처리를하기때문에 LoginFrom 을 사용하지않아 커스텀)
     * @param request 요청
     * @param response 응답
     * redirect as part of a multi-stage authentication process (such as OpenID).
     * @return authenticate 객체를 반환
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도중");
        
            ObjectMapper objectMapper = new ObjectMapper();
        Member member = null;
        try {
            member = objectMapper.readValue(request.getInputStream(), Member.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(member.getLoginId(), member.getPassword());
            Authentication authenticate = authenticationManager.authenticate(token);
            
            return authenticate;

    }

    /**
     * 로그인성공시 실행되며, 응답값에 jwt 토큰을 만들어 전달달한다. (200OK).
     * @param request 요청
     * @param response 응답
     * @param chain 필터체인
     * @param authResult the object returned from the <tt>attemptAuthentication</tt>
     * method.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){
        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();
        String loginId = principal.getMember().getLoginId();

        String token = jwtProvider.generateAccessToken(loginId);
        String refreshToken = jwtProvider.generateRefreshToken(loginId);

        response.addHeader("Authorization","Bearer "+ token);
        response.addHeader("RefreshToken","Bearer "+ refreshToken);
    }


    /**
     * 로그인 실패시 BadCredentialsException 을 날린다.
     * @param request
     * @param response
     * @param failed
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        log.error("로그인 실패", failed);
        throw new BadCredentialsException("로그인 실패");
    }
}
