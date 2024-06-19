package com.team.RecipeRadar.global.security.basic;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalService implements UserDetailsService{

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username){
        Member member = memberRepository.findByLoginId(username);
        if (member!=null) {
            return new PrincipalDetails(member);
        }else throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);
    }
}
