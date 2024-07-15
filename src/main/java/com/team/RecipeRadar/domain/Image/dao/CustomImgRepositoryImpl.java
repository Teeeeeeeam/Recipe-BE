package com.team.RecipeRadar.domain.Image.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

@Repository
@RequiredArgsConstructor
public class CustomImgRepositoryImpl implements CustomImgRepository{

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 레시피 이미지 삭제
     */
    @Override
    public void deleteImagesByRecipeId(Long recipeId) {
        jpaQueryFactory
                .delete(uploadFile)
                .where(uploadFile.recipe.id.in(recipeId)).execute();
    }

    /**
     * 레시피 아이디가 같은 저장된 파일명 추출
     */
    @Override
    public List<String> findAllStoredNamesByRecipeId(Long recipeId) {
        return jpaQueryFactory.select(uploadFile.storeFileName)
                .from(uploadFile)
                .where(uploadFile.recipe.id.eq(recipeId)).fetch();
    }


}
