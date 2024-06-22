package com.team.RecipeRadar.global.auth.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.member.domain.QMember.member;
import static com.team.RecipeRadar.global.auth.domain.QRefreshToken.*;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryCustomImpl implements RefreshTokenRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MemberDto existsByRefreshTokenAndMember(String token, String loginId) {

        List<Member> members = jpaQueryFactory
                .select(refreshToken1.member)
                .from(refreshToken1)
                .leftJoin(refreshToken1).on(refreshToken1.member.id.eq(member.id)).fetchJoin()
                .where(refreshToken1.member.loginId.eq(loginId)
                        .and(refreshToken1.refreshToken.eq(token)))
                .fetch();
        return  members.stream().map(member -> MemberDto.from(member)).findFirst().orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));

    }
}
