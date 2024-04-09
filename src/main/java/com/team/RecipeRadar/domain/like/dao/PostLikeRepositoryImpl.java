package com.team.RecipeRadar.domain.like.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.domain.QMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
;
import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.like.domain.QPostLike.*;

@Slf4j
@Repository
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public PostLikeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Slice<UserLikeDto> userInfoLikes(Long memberId, Pageable pageable) {
        List<PostLike> result = queryFactory
                .selectFrom(postLike)
                .innerJoin(postLike.member, QMember.member).fetchJoin()
                .where(postLike.member.id.eq(memberId))
                .orderBy(postLike.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1) // limit 적용
                .fetch();


        List<UserLikeDto> content = result.stream()
                .map(p -> new UserLikeDto(p.getId(), p.getPost().getPostContent(), p.getPost().getPostTitle()))
                .collect(Collectors.toList());

        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext= true;
        }

      return new SliceImpl<>(content,pageable,hasNext);
    }




}
