package com.team.RecipeRadar.global.security.basic;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    public  MemberDto getMemberDto(Member member){
        return MemberDto.from(member);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String roleName : member.getRoleList()) {
            authorities.add(new SimpleGrantedAuthority(roleName));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return member.isVerified();
    }

    @Override
    public boolean isAccountNonLocked() {
        return member.isVerified();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return member.isVerified();
    }

    @Override
    public boolean isEnabled() {
        return member.isVerified();
    }

    @Override
    public String getName() {
        return member.getLoginId();
    }

    public Long getMemberId(){
        return member.getId();
    }
}
