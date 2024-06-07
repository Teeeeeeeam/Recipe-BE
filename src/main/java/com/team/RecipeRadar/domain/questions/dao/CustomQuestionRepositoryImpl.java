package com.team.RecipeRadar.domain.questions.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.questions.domain.AnswerType;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.global.Image.domain.QUploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.questions.domain.QAnswer.answer;
import static com.team.RecipeRadar.domain.questions.domain.QQuestion.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionStatus.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionType.*;
import static com.team.RecipeRadar.global.Image.domain.QUploadFile.uploadFile;

@Slf4j
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

    @Override
    public Slice<QuestionDto> getAllQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (lasId!=null){
            builder.and(question.id.lt(lasId));
        }

        // 현재 문의 타입의 따라 정렬
        if(questionType!=null && questionType.equals(ACCOUNT_INQUIRY))
            builder.and(question.questionType.eq(ACCOUNT_INQUIRY));
        else if(questionType!=null && questionType.equals(GENERAL_INQUIRY))
            builder.and(question.questionType.eq(GENERAL_INQUIRY));

        // 현재 답변 상태 유뮤의 따라 정렬
        if(questionStatus!=null && questionStatus.equals(PENDING))
            builder.and(question.status.eq(PENDING));
        else if(questionStatus!=null && questionStatus.equals(COMPLETED))
            builder.and(question.status.eq(COMPLETED));

        List<Question> questionList = jpaQueryFactory.select(question)
                .from(question)
                .leftJoin(answer).on(answer.question.id.eq(question.id))
                .leftJoin(question.member).fetchJoin()
                .where(builder)
                .orderBy(question.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<QuestionDto> questionDtoList = questionList.stream().map(q -> QuestionDto.pageDto(q)).collect(Collectors.toList());

        boolean hasNext = false;

        if(questionDtoList.size() > pageable.getPageSize()){
            questionDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(questionDtoList,pageable,hasNext);
    }

    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(img !=null && !img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }
}
