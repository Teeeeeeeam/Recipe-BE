package com.team.RecipeRadar.domain.recipe.dao.recipe;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.recipe.domain.QIngredient;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.CookStepDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.recipe.domain.QCookingStep.*;
import static com.team.RecipeRadar.domain.recipe.domain.QIngredient.ingredient;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;
import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

@Slf4j
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
        for (String ingredientList : ingredients) {
            builder.or(ingredient.ingredients.like("%"+ingredientList+"%"));
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
     * @param ingredients   재료들
     * @param pageable      페이지정보
     * @return              pageImpl을 반환
     */
    @Override
    public Page<RecipeDto> getNormalPage(List<String> ingredients, String title, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 제목이 있을 경우 제목 조건 추가
        if (title != null) {
            builder.and(recipe.title.like("%" + title + "%"));
        }

        // 재료 목록이 있을 경우 재료 조건 추가
        if (ingredients != null && !ingredients.isEmpty()) {
            BooleanBuilder ingredientsBuilder = new BooleanBuilder();
            for (String ingredient : ingredients) {
                ingredientsBuilder.or(QIngredient.ingredient.ingredients.like("%" + ingredient + "%"));
            }
            builder.and(ingredientsBuilder);
        }

        // 결과 페칭 쿼리
        List<Tuple> result = queryFactory.select(
                recipe.title,recipe.id, uploadFile.storeFileName, recipe.likeCount, recipe.cookingTime,recipe.cookingLevel, recipe.people, recipe.createdAt)
                .from(ingredient)
                .join(ingredient.recipe, recipe)
                .join(uploadFile).on(uploadFile.recipe.id.eq(recipe.id))
                .where(builder, uploadFile.post.isNull())
                .orderBy(recipe.id.asc()) // 정렬 조건 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 카운트 쿼리
        Long count = queryFactory.select(recipe.id.count())
                .from(ingredient)
                .join(ingredient.recipe, recipe)
                .where(builder)
                .fetchOne();

        // 결과 DTO 리스트 변환
        List<RecipeDto> content = getRecipeDtoList(result);

        // 페이지 객체 생성
        return new PageImpl<>(content, pageable, count);
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
}
