package com.team.RecipeRadar.domain.questions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.domain.AnswerType;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDto {

    private Long id;

    private QuestionType questionType;

    private String title;

    private String question_content;

    private QuestionStatus status;

    private AnswerType answer;

    private String img_url;

    private String answer_email;

    private MemberDto member;


    private QuestionDto(QuestionType questionType, String title, String question_content, QuestionStatus status, AnswerType answer,String answer_email, MemberDto memberDto) {
        this.questionType = questionType;
        this.title = title;
        this.question_content = question_content;
        this.status = status;
        this.answer = answer;
        this.answer_email=answer_email;
        this.member = memberDto;
    }

    public static QuestionDto fromDto(Question question){
        return new QuestionDto(question.getQuestionType(),question.getTitle(),question.getQuestion_content(),question.getStatus(),question.getAnswer(),question.getAnswer_email(),MemberDto.from(question.getMember()));
    }

    public static QuestionDto of(Question question,String url){

        QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder()
                .title(question.getTitle())
                .answer(question.getAnswer())
                .questionType(question.getQuestionType())
                .question_content(question.getQuestion_content())
                .status(question.getStatus());


        if (question.getMember()!=null){
            MemberDto memberDto = MemberDto.builder().id(question.getMember().getId()).nickname(question.getMember().getNickName()).loginId(question.getMember().getLoginId()).build();
            questionDtoBuilder.member(memberDto);
        }
        if (question.getAnswer_email()!=null){
            questionDtoBuilder.answer_email(question.getAnswer_email());
        }
        if(url!=null){
            questionDtoBuilder.img_url(url);
        }

        return questionDtoBuilder.build();
    }
}
