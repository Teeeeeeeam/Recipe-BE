package com.team.RecipeRadar.domain.like.dao.like;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RecipeLikeRepositoryImpl implements RecipeLikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /**
     * Slice 이용한 무한 페이징
     */
    @Override
    public Slice<UserLikeDto> userInfoRecipeLikes(Long memberId, Long recipeLike_lastId,Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(recipeLike_lastId!=null){
            builder.and(recipeLike.id.lt(recipeLike_lastId));
        }
        List<RecipeLike> likeList = queryFactory.selectFrom(recipeLike)
                .innerJoin(recipeLike.member, member).fetchJoin()
                .where(builder,recipeLike.member.id.eq(memberId))
                .orderBy(recipeLike.id.desc())
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

    @Override
    public void deleteRecipeId(Long recipeId) {
        queryFactory.delete(recipeLike)
                .where(recipeLike.recipe.id.in(recipeId)).execute();
    }
}
