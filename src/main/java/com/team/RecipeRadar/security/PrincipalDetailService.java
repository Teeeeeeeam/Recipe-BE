package com.team.RecipeRadar.security;

import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByLoginId(username);

            if (member!=null) {
                return new PrincipalDetails(member);
            }else throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
    }

