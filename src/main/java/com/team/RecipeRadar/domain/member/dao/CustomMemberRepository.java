package com.team.RecipeRadar.domain.member.dao;


import com.team.RecipeRadar.domain.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomMemberRepository {

    Slice<Member> getMemberInfo(Pageable pageable);
}
