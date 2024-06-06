package com.team.RecipeRadar.domain.member.dao;


import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomMemberRepository {

    Slice<MemberDto> getMemberInfo(Long lastMemberId,Pageable pageable);

    Slice<MemberDto> searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable);

    List<Member> adminMember();
}
