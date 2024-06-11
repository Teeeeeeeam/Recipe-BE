package com.team.RecipeRadar.domain.questions.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
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

import static com.team.RecipeRadar.domain.questions.domain.QQuestion.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionStatus.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionType.*;
import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

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

        dynamicQuery(lasId, questionType, questionStatus, builder);

        List<Question> questionList = jpaQueryFactory.select(question)
                .from(question)
                .leftJoin(question.member).fetchJoin()
                .where(builder)
                .orderBy(question.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<QuestionDto> questionDtoList = questionList.stream().map(q -> QuestionDto.pageDto(q)).collect(Collectors.toList());

        boolean hasNext = isHasNext(pageable, questionDtoList);

        return new SliceImpl<>(questionDtoList,pageable,hasNext);
    }

    /**
     * 사용자 페이지에서 사용자가 작성한 문의사항의 대해서 조회
     */
    @Override
    public Slice<QuestionDto> getUserAllQuestion(Long lastId, Long memberId,QuestionType questionType, QuestionStatus questionStatus,Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        dynamicQuery(lastId, questionType, questionStatus, builder);

        List<Question> questionList = jpaQueryFactory.select(question)
                .from(question)
                .leftJoin(question.member).fetchJoin()
                .where(builder, question.member.id.eq(memberId))
                .orderBy(question.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<QuestionDto> questionDtoList = questionList.stream().map(q -> QuestionDto.pageDto(q)).collect(Collectors.toList());

        boolean hasNext = isHasNext(pageable, questionDtoList);

        return new SliceImpl<>(questionDtoList,pageable,hasNext);
    }


    private static void dynamicQuery(Long lasId, QuestionType questionType, QuestionStatus questionStatus, BooleanBuilder builder) {
        if (lasId !=null){
            builder.and(question.id.lt(lasId));
        }
        // 현재 문의 타입의 따라 정렬
        if(questionType !=null && questionType.equals(ACCOUNT_INQUIRY))
            builder.and(question.questionType.eq(ACCOUNT_INQUIRY));
        else if(questionType !=null && questionType.equals(GENERAL_INQUIRY))
            builder.and(question.questionType.eq(GENERAL_INQUIRY));

        // 현재 답변 상태 유뮤의 따라 정렬
        if(questionStatus !=null && questionStatus.equals(PENDING))
            builder.and(question.status.eq(PENDING));
        else if(questionStatus !=null && questionStatus.equals(COMPLETED))
            builder.and(question.status.eq(COMPLETED));
    }

    private static boolean isHasNext(Pageable pageable, List<QuestionDto> questionDtoList) {
        boolean hasNext = false;

        if(questionDtoList.size() > pageable.getPageSize()){
            questionDtoList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }

    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(img !=null && !img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }
}
