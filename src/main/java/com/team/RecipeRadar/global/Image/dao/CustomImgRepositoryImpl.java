package com.team.RecipeRadar.global.Image.dao;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.recipe.domain.QRecipe;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.domain.recipe.domain.QRecipe.*;
import static com.team.RecipeRadar.global.Image.domain.QUploadFile.*;

@Repository
@RequiredArgsConstructor
public class CustomImgRepositoryImpl implements CustomImgRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final S3UploadService s3UploadService;

    @Override
    public void deleteMemberImg(Long memberId) {

        jpaQueryFactory.delete(uploadFile)
                .where(uploadFile.post.id.in(
                        JPAExpressions
                                .select(post.id).from(post).where(post.member.id.eq(memberId)))
                ).execute();
    }

    @Override
    public void delete_recipe_img(Long recipeId) {
        jpaQueryFactory
                .delete(uploadFile)
                .where(uploadFile.recipe.id.in(recipeId)).execute();
    }

    @Override
    public List<String> findAllStoredName(Long recipeId) {
        return jpaQueryFactory.select(uploadFile.storeFileName)
                .from(uploadFile)
                .where(uploadFile.recipe.id.eq(recipeId)).fetch();
    }


}
