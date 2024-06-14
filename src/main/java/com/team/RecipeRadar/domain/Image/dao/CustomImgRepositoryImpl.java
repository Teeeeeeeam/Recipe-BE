package com.team.RecipeRadar.domain.Image.dao;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;

@Repository
@RequiredArgsConstructor
public class CustomImgRepositoryImpl implements CustomImgRepository{

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 사용자Id와 같은 이미지를 삭제
     */
    @Override
    public void deleteMemberImg(Long memberId) {
        jpaQueryFactory.delete(uploadFile)
                .where(uploadFile.post.id.in(
                        JPAExpressions
                                .select(post.id).from(post).where(post.member.id.eq(memberId)))
                ).execute();
    }

    /**
     * 레시피 이미지 삭제
     */
    @Override
    public void delete_recipe_img(Long recipeId) {
        jpaQueryFactory
                .delete(uploadFile)
                .where(uploadFile.recipe.id.in(recipeId)).execute();
    }

    /**
     * 레시피 아이디가 같은 저장된 파일명 추출
     */
    @Override
    public List<String> findAllStoredName(Long recipeId) {
        return jpaQueryFactory.select(uploadFile.storeFileName)
                .from(uploadFile)
                .where(uploadFile.recipe.id.eq(recipeId)).fetch();
    }


}
