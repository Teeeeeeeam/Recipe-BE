package com.team.RecipeRadar.global.security;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import lombok.RequiredArgsConstructor;
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

