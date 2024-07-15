package com.team.RecipeRadar.global.security.jwt.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtHeader.replace("Bearer ", "");
        try {
            if (jwtProvider.TokenExpiration(jwtToken)) {
                throw new JwtTokenException("토큰이 만료되었습니다.");
            }

            String loginId = jwtProvider.validateAccessToken(jwtToken);
            if (loginId != null) {
                Member member = memberRepository.findByLoginId(loginId);
                PrincipalDetails principalDetails = new PrincipalDetails(member);
                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } else {
                throw new JwtTokenException("유효하지 않은 토큰입니다.");
            }
        } catch (JWTDecodeException e) {
            log.error("JWT 토큰을 디코딩하는 중에 오류가 발생했습니다.", e);
            throw new JwtTokenException("JWT 토큰을 디코딩하는 중에 오류가 발생했습니다.");
        }
    }
}
