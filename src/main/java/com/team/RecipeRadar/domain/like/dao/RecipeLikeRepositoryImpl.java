package com.team.RecipeRadar.domain.like.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.like.domain.QRecipeLike.*;
import static com.team.RecipeRadar.domain.member.domain.QMember.*;


@Slf4j
@Repository
public class RecipeLikeRepositoryImpl implements RecipeLikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RecipeLikeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    /**
     * Slice 이용한 무한 페이징 querydsl
     * @param memberId  사용자의 id
     * @param pageable  
     * @return  new SliceImpl<> 페이지 정보 반환
     */
    @Override
    public Slice<UserLikeDto> userInfoRecipeLikes(Long memberId, Pageable pageable) {
        List<RecipeLike> likeList = queryFactory.selectFrom(recipeLike)
                .innerJoin(recipeLike.member, member).fetchJoin()
                .where(recipeLike.member.id.eq(memberId))
                .orderBy(recipeLike.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        List<UserLikeDto> content = likeList.stream()
                .map(UserLikeDto::Recipe_of) // 이 부분을 수정
                .collect(Collectors.toList());

        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext= true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
