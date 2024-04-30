package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.recipe.domain.QIngredient.*;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomRecipeRepositoryImpl implements CustomRecipeRepository{

    private final JPAQueryFactory queryFactory;

    /**
     * 동적(재료) 무한 스크르롤 페이징 기능
     * @param ingredients       List<String> 재료값
     * @param pageable          페이징
     * @return                  Slice반환
     */
    @Override
    public Slice<RecipeDto> getRecipe(List<String> ingredients, Pageable pageable) {
        
        //동적 쿼리 생성 레시피 list 에서 재료를 하나씩 or like() 문으로 처리
        BooleanBuilder builder = new BooleanBuilder();
        for (String ingredientList : ingredients) {
            builder.or(ingredient.ingredients.like("%"+ingredientList+"%"));
        }

        List<Tuple> result = queryFactory.select(recipe.title, recipe.id, recipe.imageUrl, recipe.likeCount, recipe.cookingTime, recipe.cookingLevel,recipe.people)
                .from(ingredient)
                .join(ingredient.recipe,recipe)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        List<RecipeDto> content = result.stream().map(tuple -> RecipeDto.of(tuple.get(recipe.id), tuple.get(recipe.imageUrl), tuple.get(recipe.title), tuple.get(recipe.cookingLevel),
                tuple.get(recipe.people), tuple.get(recipe.cookingTime), tuple.get(recipe.likeCount))).collect(Collectors.toList());
        
        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content,pageable,hasNext);
    }
}
