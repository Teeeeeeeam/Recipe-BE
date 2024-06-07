package com.team.RecipeRadar.domain.questions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.domain.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "answer,member")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionDto {

    private Long id;

    private QuestionType questionType;

    private String title;

    private String question_content;

    private QuestionStatus status;

    private AnswerType answerType;

    private LocalDateTime create_at;

    private LocalDateTime answer_at;

    private String img_url;

    private String answer_email;

    private MemberDto member;

    private AnswerDto answer;


    private QuestionDto(QuestionType questionType, String title, String question_content, QuestionStatus status, AnswerType answer,String answer_email, MemberDto memberDto) {
        this.questionType = questionType;
        this.title = title;
        this.question_content = question_content;
        this.status = status;
        this.answerType = answer;
        this.answer_email=answer_email;
        this.member = memberDto;
    }

    public static QuestionDto fromDto(Question question){
//        MemberDto.builder().
        return new QuestionDto(question.getQuestionType(),question.getTitle(),question.getQuestion_content(),question.getStatus(),question.getAnswer(),question.getAnswer_email(),MemberDto.from(question.getMember()));
    }

    public static QuestionDto of(Question question, String url){

        QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder()
                .title(question.getTitle())
                .answerType(question.getAnswer())
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

   public static QuestionDto of(Question question, Answer answer, String img_url){
       LocalDateTime createdDate = question.getCreatedDate();
       MemberDto memberDto = MemberDto.builder().id(question.getMember().getId()).build();
       QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder().id(question.getId())
               .status(question.getStatus()).title(question.getTitle())
               .member(memberDto)
               .question_content(question.getQuestion_content())
               .create_at(createdDate.withSecond(0).withNano(0));

       if(img_url!=null){
           questionDtoBuilder.img_url(img_url);
       }

       if(answer!=null) {
           questionDtoBuilder.answer(AnswerDto.fromDto(answer));
           questionDtoBuilder.answer_at(createdDate.withSecond(0).withNano(0));
       }
       return questionDtoBuilder.build();
   }
}
