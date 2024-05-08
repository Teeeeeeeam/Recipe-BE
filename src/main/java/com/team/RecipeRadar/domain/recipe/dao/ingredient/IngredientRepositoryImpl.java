package com.team.RecipeRadar.domain.recipe.dao.ingredient;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.domain.QMember;
import com.team.RecipeRadar.domain.recipe.domain.QIngredient;
import com.team.RecipeRadar.domain.recipe.domain.QRecipe;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.recipe.domain.QIngredient.*;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Recipe> searchRecipeByIngredient() {


        QRecipe qRecipe = QRecipe.recipe;
        List<Recipe> fetch = jpaQueryFactory
                .selectFrom(qRecipe)
                .limit(5)
                .fetch();
        return fetch;

    }

    public void updateRecipe_ing(Long recipe_id,String ings){

        jpaQueryFactory.update(ingredient)
                .set(ingredient.ingredients,ings)
                .where(ingredient.recipe.id.eq(recipe_id)).execute();
    }
}
