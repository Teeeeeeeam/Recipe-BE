package com.team.RecipeRadar.domain.member.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.member.domain.QMember.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Slice getMemberInfo(Long lastMemberId,Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if(lastMemberId!=null){
            builder.and(member.id.gt(lastMemberId));
        }

        List<Tuple> list = jpaQueryFactory.select(member.id,member.loginId, member.nickName, member.email, member.createAt, member.username)
                .from(member)
                .where(builder)
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<MemberDto> memberDtoList = list.stream().map(tuple -> MemberDto.of(tuple.get(member.id), tuple.get(member.loginId), tuple.get(member.email), tuple.get(member.username),
                        tuple.get(member.nickName), tuple.get(member.createAt)))
                .collect(Collectors.toList());


        boolean hasNext = isHasNext(pageable, memberDtoList);

        return new SliceImpl<>(memberDtoList,pageable,hasNext);
    }

    @Override
    public Slice<MemberDto> searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (loginId != null) {
            builder.and(member.loginId.containsIgnoreCase(loginId));
        }
        if (nickname != null) {
            builder.and(member.nickName.containsIgnoreCase(nickname));
        }
        if (email != null) {
            builder.and(member.email.containsIgnoreCase(email));
        }
        if (username != null) {
            builder.and(member.username.containsIgnoreCase(username));
        }
        if (lastMemberId != null) {
            builder.and(member.id.gt(lastMemberId));
        }

        List<Member> memberList = jpaQueryFactory.select(member)
                .from(member)
                .where(builder)
                .limit(pageable.getPageSize()+1)
                .fetch();


        List<MemberDto> memberDtoList = memberList.stream().map(m -> MemberDto.of(m.getId(), m.getLoginId(), m.getEmail(), m.getUsername(), m.getNickName(), m.getCreateAt())).collect(Collectors.toList());
        boolean hasNext = isHasNext(pageable, memberDtoList);


        return new SliceImpl<>(memberDtoList,pageable,hasNext);
    }

    @Override
    public List<Member> adminMember() {

        List<Member> memberList = jpaQueryFactory.selectFrom(member)
                .where(member.roles.in("ROLE_ADMIN")).fetch();
        return memberList;
    }

    private boolean isHasNext(Pageable pageable, List<MemberDto> content) {
        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }
}
