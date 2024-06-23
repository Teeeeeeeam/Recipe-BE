package com.team.RecipeRadar.domain.like.dao.like;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
;
import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.like.domain.QPostLike.*;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /**
     * Slice 이용한 무한 페이징
     */
    @Override
    public Slice<UserLikeDto> userInfoLikes(Long memberId, Long postLike_lastId,Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(postLike_lastId!=null){
            builder.and(postLike.id.lt(postLike_lastId));
        }

        List<PostLike> postLikeList = queryFactory
                .selectFrom(postLike)
                .innerJoin(postLike.member, QMember.member).fetchJoin()
                .where(builder,postLike.member.id.eq(memberId))
                .orderBy(postLike.id.desc())
                .limit(pageable.getPageSize()+1) // limit 적용
                .fetch();


        List<UserLikeDto> content = postLikeList.stream()
                .map(UserLikeDto::Post_of)
                .collect(Collectors.toList());

        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext= true;
        }

      return new SliceImpl<>(content,pageable,hasNext);
    }

    /**
     * 레시피 아이디와 일치하는 게시글 좋아요 삭제
     */
    @Override
    public void deleteRecipeId(Long recipeId) {
        queryFactory
                .delete(postLike)
                .where(postLike.post.id.in(
                        JPAExpressions.select(post.id)
                                .from(post)
                                .join(recipe).on(post.recipe.id.eq(recipe.id))
                                .where(recipe.id.eq(recipeId))
                )).execute();
    }
}
