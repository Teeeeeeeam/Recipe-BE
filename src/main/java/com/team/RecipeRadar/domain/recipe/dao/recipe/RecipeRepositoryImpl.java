//package com.team.RecipeRadar.domain.recipe.dao.recipe;
//
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.team.RecipeRadar.domain.recipe.domain.QIngredient;
//import com.team.RecipeRadar.domain.recipe.domain.QRecipe;
//import com.team.RecipeRadar.domain.recipe.domain.Recipe;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import javax.persistence.EntityManager;
//import java.util.List;
//
//@Repository
//public class RecipeRepositoryImpl implements RecipeRepositoryCustom{
//
//    private final JPAQueryFactory jpaQueryFactory;
//    private final EntityManager entityManager;
//
//    public RecipeRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
//        this.jpaQueryFactory = jpaQueryFactory;
//        this.entityManager = entityManager;
//    }
//
//
//    public Page<Recipe> findRecipeByIngredient(List<String> ingredientTitles, Pageable pageable) {
//        QRecipe recipe = QRecipe.recipe;
//        QIngredient ingredient = QIngredient.ingredient;
//        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//        BooleanBuilder conditionBuilder = new BooleanBuilder();
//
//
//        for (String title : ingredientTitles) {
//            conditionBuilder.and(ingredient.ingredientAndQuantity.containsIgnoreCase(title));
//        }
//
//
//        List<Recipe> recipes = queryFactory.selectFrom(recipe)
//                .leftJoin(recipe.ingredients, ingredient)
//                .where(conditionBuilder)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//
//        long totalCount = queryFactory.selectFrom(recipe)
//                .leftJoin(recipe.ingredients, ingredient)
//                .where(conditionBuilder)
//                .fetchCount();
//
//        return new PageImpl<>(recipes, pageable, totalCount);
//    }
//}
