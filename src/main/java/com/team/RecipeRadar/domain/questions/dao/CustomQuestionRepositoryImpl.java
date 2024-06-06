package com.team.RecipeRadar.domain.questions.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

import static com.team.RecipeRadar.domain.questions.domain.QQuestion.*;
import static com.team.RecipeRadar.global.Image.domain.QUploadFile.uploadFile;

@Repository
@RequiredArgsConstructor
public class CustomQuestionRepositoryImpl implements CustomQuestionRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Value("${S3.URL}")
    private  String s3URL;

    @Override
    public QuestionDto details(Long questionId) {

        List<Tuple> questonList = jpaQueryFactory.select(question, uploadFile.storeFileName)
                .from(question)
                .leftJoin(uploadFile).on(uploadFile.question.id.eq(question.id))
                .where(question.id.eq(questionId)).fetch();

        return questonList.stream().map(tuple -> QuestionDto.of(tuple.get(question),getImageUrl(tuple))).findFirst().orElseThrow(() -> new NoSuchElementException("문의사항을 찾을수 없습니다."));
    }

    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(img !=null && !img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }
}
