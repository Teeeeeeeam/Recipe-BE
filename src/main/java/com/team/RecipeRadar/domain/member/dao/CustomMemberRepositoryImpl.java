package com.team.RecipeRadar.domain.member.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.member.domain.QMember.*;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final String FULL_TEXT_INDEX ="function('match',{0},{1})";
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

        addSearchCondition(builder, loginId, member.loginId);
        addSearchCondition(builder, nickname, member.nickName);
        addSearchCondition(builder, email, member.email);
        addSearchCondition(builder, username, member.username);

        if (lastMemberId != null) {
            builder.and(member.id.lt(lastMemberId));
        }

        List<Member> memberList = jpaQueryFactory.select(member)
                .from(member)
                .where(builder)
                .orderBy(member.id.desc())
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

    /* 동적 회원 게시글 검색 where 문 */
    private void addSearchCondition(BooleanBuilder builder, String searchValue, StringPath searchField) {

        if (searchValue != null) {
            long count  = dataCount(searchValue, searchField);      // 총 데이터 수 조회
            if(count<1000) {
                NumberTemplate<Double> searchTemplate = Expressions.numberTemplate(Double.class,
                        FULL_TEXT_INDEX, searchField, searchValue);
                builder.and(searchTemplate.gt(0));
            }else{
                builder.and(searchField.like("%"+searchValue+"%"));
            }
        }
    }

    private long dataCount(String searchValue,StringPath searchField) {
       return jpaQueryFactory.select(member.count())
                .from(member)
                .where(searchField.like("%"+searchValue+"%")).fetchOne();
    }

}
