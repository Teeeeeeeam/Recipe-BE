package com.team.RecipeRadar.domain.member.dao;


import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMemberRepository {

    Slice<MemberDto> getMemberInfo(Pageable pageable);

    Slice<MemberDto> searchMember(String loginId, String nickname, String email, String username,Pageable pageable);
}
