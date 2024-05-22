package com.team.RecipeRadar.domain.member.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.member.domain.QMember.*;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Slice getMemberInfo(Pageable pageable) {

        List<Member> memberList = jpaQueryFactory.select(member)
                .from(member)
                .limit(pageable.getPageSize()+1)
                .fetch();

        boolean hasNext = isHasNext(pageable, memberList);

        return new SliceImpl<>(memberList,pageable,hasNext);
    }

    private boolean isHasNext(Pageable pageable, List<Member> content) {
        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }
}
