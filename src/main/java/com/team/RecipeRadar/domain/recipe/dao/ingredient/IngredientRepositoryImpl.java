package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.recipe.domain.QIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    public void updateRecipe_ing(Long recipe_id,String ingredient){

        jpaQueryFactory.update(QIngredient.ingredient)
                .set(QIngredient.ingredient.ingredients, ingredient)
                .where(QIngredient.ingredient.recipe.id.eq(recipe_id)).execute();
    }
}
