package com.team.RecipeRadar.domain.questions.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.team.RecipeRadar.domain.questions.domain.QAnswer.*;
import static com.team.RecipeRadar.domain.questions.domain.QQuestion.*;
import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomAnswerRepositoryImpl implements CustomAnswerRepository{

    private final JPAQueryFactory  jpaQueryFactory;

    @Value("${S3.URL}")
    private  String s3URL;

    @Override
    public QuestionDto viewResponse(Long questionId) {

        List<Tuple> result = jpaQueryFactory
                .select(question, answer, uploadFile.storeFileName)
                .from(question)
                .leftJoin(answer).on(answer.question.id.eq(question.id))
                .leftJoin(uploadFile).on(uploadFile.question.id.eq(question.id))
                .where(question.id.eq(questionId))
                .fetch();

         return result.stream()
                .map(tuple -> QuestionDto.of(tuple.get(question), tuple.get(answer), getImageUrl(tuple))).findFirst().orElseThrow(() -> new BadRequestException("해당 문의사항을 찾을 수 없습니다."));
    }
    private String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(img !=null && !img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }

}
