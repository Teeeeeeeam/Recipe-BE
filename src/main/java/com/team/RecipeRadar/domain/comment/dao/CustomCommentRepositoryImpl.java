package com.team.RecipeRadar.domain.comment.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.comment.domain.QComment.*;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository{

    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 작성한 게시글의 대한 댓글을 모두 조회하는하는 로직
     * @return
     */
    @Override
    public Slice<CommentDto> getPostComment(Long postId, Long lastId, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        if(lastId!=null){
            builder.and(comment.id.gt(lastId));
        }
        List<Comment> comments = jpaQueryFactory.select(comment)
                .from(comment)
                .where(builder,comment.post.id.eq(postId))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<CommentDto> commentDtoList = comments.stream().map(CommentDto::admin).collect(Collectors.toList());
        boolean hasNext =false;

        if (commentDtoList.size()>pageable.getPageSize()) {
            commentDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(commentDtoList,pageable,hasNext);
    }

    @Override
    public void delete_post(Long recipeId) {
        jpaQueryFactory.delete(comment)
                .where(comment.post.id.in(
                        JPAExpressions
                                .select(post.id).from(post)
                                .join(recipe).on(post.recipe.id.eq(recipe.id))
                                .where(post.recipe.id.eq(recipeId))
                )).execute();
    }

    @Override
    public void deleteMember_comment(Long memberId) {
        jpaQueryFactory.delete(comment)
                .where(comment.post.id.in(
                        JPAExpressions
                                .select(post.id).from(post).where(post.member.id.eq(memberId)))
                ).execute();
    }
}

