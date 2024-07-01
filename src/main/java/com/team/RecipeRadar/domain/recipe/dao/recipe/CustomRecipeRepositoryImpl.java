package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.recipe.api.user.OrderType;
import com.team.RecipeRadar.domain.recipe.domain.QIngredient;
import com.team.RecipeRadar.domain.recipe.domain.QRecipe;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.CookStepDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.recipe.domain.QCookingStep.*;
import static com.team.RecipeRadar.domain.recipe.domain.QIngredient.ingredient;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;
import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

@Repository
@RequiredArgsConstructor
public class CustomRecipeRepositoryImpl implements CustomRecipeRepository{

    private final JPAQueryFactory queryFactory;

    @Value("${S3.URL}")
    private  String s3URL;

    /**
     * 동적(재료) 무한 스크르롤 페이징 기능
     * @param ingredients       List<String> 재료값
     * @param pageable          페이징
     * @return                  Slice반환
     */
    @Override
    public Slice<RecipeDto> getRecipe(List<String> ingredients,Long lastRecipeId,Pageable pageable) {
        
        //동적 쿼리 생성 레시피 list 에서 재료를 하나씩 or like() 문으로 처리
        BooleanBuilder builder = new BooleanBuilder();
        if (ingredients != null && !ingredients.isEmpty()) {
            for (String ingredientList : ingredients) {
                builder.or(ingredient.ingredients.like("%" + ingredientList + "%"));
            }
        }
        // 마지막 레시피 아이디 값을 동해 페이지 유뮤 판단
        if (lastRecipeId!=null){
            builder.and(recipe.id.gt(lastRecipeId));
        }

        List<Tuple> result = getSearchRecipe(pageable, builder);

        List<RecipeDto> content = getRecipeDtoList(result);

        boolean hasNext = isHasNext(pageable, content);

        return new SliceImpl<>(content,pageable,hasNext);
    }

    /*
    기존 재료 검색 로직이랑 비슷하나 제목 검색의 대한 조건이 추가된 어드민 전용 로직
     */

    @Override
    public Slice<RecipeDto> adminSearchTitleOrIng(List<String> ingredients, String title,Long lastRecipeId, Pageable pageable) {
        //동적 쿼리 생성 레시피 list 에서 재료를 하나씩 or like() 문으로 처리
        BooleanBuilder builder = new BooleanBuilder();

        if (title!=null){
            builder.and(recipe.title.like("%"+title+"%"));
        }
        if (ingredients!=null) {
            for (String ingredientList : ingredients) {
                builder.and(ingredient.ingredients.like("%" + ingredientList + "%"));
            }
        }
        // 마지막 레시피 아이디 값을 동해 페이지 유뮤 판단
        if (lastRecipeId!=null){
            builder.and(recipe.id.gt(lastRecipeId));
        }

        List<Tuple> result = getSearchRecipe(pageable, builder);

        List<RecipeDto> content = getRecipeDtoList(result);

        boolean hasNext = isHasNext(pageable, content);

        return new SliceImpl<>(content,pageable,hasNext);
    }
    /**
     * 재료에 대한 일반 페이징 쿼리 (무한스크롤방식과, 일반 페이지네이션의둘중하나를 선택해하기떄문에 추후에 둘중하나는 변경가능)
     */
    @Override
    public Slice<RecipeDto> userSearchRecipe(List<String> ingredients,List<CookIngredients> cookIngredients,List<CookMethods> cookMethods,List<DishTypes> dishTypes, String title,
                                             OrderType order, Integer likeCount, Long lastId,Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder cookIngredientBuilder = new BooleanBuilder();
        BooleanBuilder ingredientsBuilder = new BooleanBuilder();
        BooleanBuilder cookMethodsBuilder = new BooleanBuilder();
        BooleanBuilder dishTypeBuilder = new BooleanBuilder();
        BooleanBuilder likeConditions = new BooleanBuilder();

        if(lastId!=null){
            builder.and(recipe.id.lt(lastId));
        }
        // Title 조건 추가
        if (title != null) {
            builder.and(recipe.title.like("%" + title + "%"));
        }

        // Cooking Ingredients 조건 추가
        if (cookIngredients != null) {
            cookIngredients.forEach(cookIngredient ->
                    cookIngredientBuilder.or(recipe.cookingIngredients.eq(cookIngredient))
            );
            builder.and(cookIngredientBuilder);
        }

        // Cook Methods 조건 추가
        if (cookMethods != null) {
            cookMethods.forEach(cookMethod ->
                    cookMethodsBuilder.or(recipe.cookMethods.eq(cookMethod))
            );
            builder.and(cookMethodsBuilder);
        }

        // Dish Types 조건 추가
        if (dishTypes != null) {
            dishTypes.forEach(dishType ->
                    dishTypeBuilder.or(recipe.types.eq(dishType))
            );
            builder.and(dishTypeBuilder);
        }

        // Ingredients 조건 추가
        if (ingredients != null && !ingredients.isEmpty()) {
            ingredients.forEach(ingredient ->
                    ingredientsBuilder.or(QIngredient.ingredient.ingredients.like("%" + ingredient + "%")));
            builder.and(ingredientsBuilder);
        }

        if (order.equals(OrderType.LIKE) && likeCount != null) {
            if (likeCount != 0) {
                likeConditions.or(recipe.likeCount.lt(likeCount));
            } else {
                likeConditions.or(recipe.likeCount.eq(0).and(recipe.id.lt(lastId)));
            }
            builder.and(likeConditions);
        } else if (!order.equals(OrderType.LIKE) && lastId != null) {
            builder.and(recipe.id.lt(lastId));
        }

        OrderSpecifier[] orderSort = getOrder(order, recipe);
        List<Tuple> result = queryFactory.select(
                        recipe.title, recipe.id, uploadFile.storeFileName, recipe.likeCount, recipe.cookingTime, recipe.cookingLevel, recipe.people, recipe.createdAt)
                .from(recipe)
                .join(ingredient).on(ingredient.recipe.id.eq(recipe.id))
                .join(uploadFile).on(uploadFile.recipe.id.eq(recipe.id))
                .where(builder, uploadFile.post.isNull())
                .orderBy(orderSort)
                .limit(pageable.getPageSize()+1)
                .fetch();

        // 결과 DTO 리스트 변환
        List<RecipeDto> content = getRecipeDtoList(result);
        boolean hasNext = isHasNext(pageable, content);

        return new SliceImpl<>(content, pageable,hasNext);
    }

    /**
     * 재료 조리순서 를 조인해서 해당 데이터를 가져와서 RecipeDto로 변환
     * @param recipeId
     * @return
     */
    @Override
    public RecipeDto getRecipeDetails(Long recipeId) {

        List<Tuple> details = queryFactory.select(recipe, uploadFile.storeFileName,ingredient.ingredients, cookingStep)
                .from(recipe)
                .join(ingredient).on(ingredient.recipe.id.eq(recipe.id))
                .join(uploadFile).on(uploadFile.recipe.id.eq(recipe.id))
                .leftJoin(recipe.cookingStepList, cookingStep)
                .where(recipe.id.eq(recipeId).and(uploadFile.post.id.isNull())).fetch();

        List<CookStepDto> cookStepDtoList = details.stream().map(tuple -> CookStepDto.of(tuple.get(cookingStep))).collect(Collectors.toList());

        String imgName = details.stream().map(tuple -> getImageUrl(tuple)).findFirst().get();
        Recipe recipeEntity = details.stream().map(tuple -> tuple.get(recipe)).collect(Collectors.toList()).stream().findFirst().get();

        String ingredients = details.stream().map(tuple -> tuple.get(ingredient.ingredients)).collect(Collectors.toList()).stream().findFirst().get();

        return RecipeDto.of(recipeEntity,imgName,cookStepDtoList,ingredients);
    }

    @Override
    public List<RecipeDto> mainPageRecipe() {

        List<Tuple> list = queryFactory.select(recipe.title, recipe.id, uploadFile.storeFileName, recipe.likeCount, recipe.cookingTime, recipe.cookingLevel, recipe.people,recipe.createdAt)
                .from(recipe)
                .join(uploadFile).on(uploadFile.recipe.id.eq(recipe.id))
                .where(uploadFile.post.id.isNull())
                .orderBy(recipe.likeCount.desc())
                .limit(8).fetch();

        return list.stream().map(tuple -> RecipeDto.from(tuple.get(recipe.id),
                getImageUrl(tuple), tuple.get(recipe.title), tuple.get(recipe.cookingLevel),
                tuple.get(recipe.people), tuple.get(recipe.cookingTime), tuple.get(recipe.likeCount),tuple.get(recipe.createdAt))).collect(Collectors.toList());
    }

    private List<Tuple> getSearchRecipe(Pageable pageable, BooleanBuilder builder) {
        List<Tuple> result = queryFactory.select(recipe.title, recipe.id, uploadFile.storeFileName, recipe.likeCount, recipe.cookingTime, recipe.cookingLevel,recipe.people,recipe.createdAt)
                .from(ingredient)
                .join(ingredient.recipe,recipe)
                .join(uploadFile).on(uploadFile.recipe.id.eq(recipe.id))
                .where(builder,uploadFile.post.isNull())
                .orderBy(recipe.id.asc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return result;
    }

    private List<RecipeDto> getRecipeDtoList(List<Tuple> result) {
        List<RecipeDto> content = result.stream().map(tuple -> RecipeDto.from(tuple.get(recipe.id), getImageUrl(tuple), tuple.get(recipe.title), tuple.get(recipe.cookingLevel),
                tuple.get(recipe.people), tuple.get(recipe.cookingTime), tuple.get(recipe.likeCount),tuple.get(recipe.createdAt))).collect(Collectors.toList());
        return content;
    }

    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(img!=null && !img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }
    private static boolean isHasNext(Pageable pageable, List<RecipeDto> content) {
        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }

    /* 동적 정렬 메서드*/
    private OrderSpecifier[] getOrder(OrderType order, QRecipe recipe) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        switch (order) {
            case LIKE:
                OrderSpecifier<Integer> orderByLikeCount = new CaseBuilder()
                        .when(recipe.likeCount.gt(0)).then(recipe.likeCount)
                                .otherwise(Expressions.constant(0))
                                        .desc();

                OrderSpecifier<Long> orderByRecipeId = new CaseBuilder()
                        .when(recipe.likeCount.eq(0)).then(recipe.id)
                        .otherwise(Expressions.constant(Long.MAX_VALUE))
                        .desc();
                orderSpecifiers.add(orderByLikeCount);
                orderSpecifiers.add(orderByRecipeId);
                break;
            case DATE:
                orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, recipe.id));
                break;
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
