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

public class CustomContextHolderAdmin implements WithSecurityContextFactory<CustomMockAdmin> {
    @Override
    public SecurityContext createSecurityContext(CustomMockAdmin annotation) {

        String loginId = annotation.loginId();
        Member member = Member.builder().loginId(loginId).username("admin").password("1234").email("test@email.com").build();
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication token = new UsernamePasswordAuthenticationToken(principalDetails, "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
