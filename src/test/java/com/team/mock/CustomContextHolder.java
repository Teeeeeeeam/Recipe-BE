package com.team.mock;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

//시큐리티 컨텍스트 홀더에 목 데이터 삽입
public class CustomContextHolder implements WithSecurityContextFactory<CustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(CustomMockUser annotation) {
        String loginId = annotation.loginId();
        Authentication token = new UsernamePasswordAuthenticationToken(loginId, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
