package com.team.RecipeRadar.domain.recipe.dao.bookmark;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipeBookmark.*;

@Repository
@RequiredArgsConstructor
public class CustomRecipeBookmarkRepositoryImpl implements CustomRecipeBookmarkRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<RecipeDto> userInfoBookmarks(Long memberId, Long lastId,Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        if(lastId!=null){
            builder.and(recipe.id.gt(lastId));
        }
        List<Tuple> list = jpaQueryFactory.select(recipe.title,recipe.id).
                from(recipeBookmark)
                .join(recipe).on(recipeBookmark.recipe.id.eq(recipe.id))
                .where(builder, recipeBookmark.member.id.eq(memberId))
                .limit(pageable.getPageSize()+1)
                .fetch();

        List<RecipeDto> recipeDtoList = list.stream().map(tuple -> RecipeDto.builder().id(tuple.get(recipe.id)).title(tuple.get(recipe.title)).build()).collect(Collectors.toList());

        boolean hasNext = false;
        if(recipeDtoList.size()> pageable.getPageSize()){
            recipeDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return  new SliceImpl<>(recipeDtoList,pageable,hasNext);
    }


}
