package com.team.RecipeRadar.domain.questions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.domain.Member;
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

    public static QuestionDto pageDto(Question question){

        Member questionMember = question.getMember();
        QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .create_at(question.getCreatedAt())
                .status(question.getStatus())
                .questionType(question.getQuestionType());
        MemberDto.MemberDtoBuilder memberDtoBuilder = MemberDto.builder();

        if(questionMember == null){
            memberDtoBuilder.loginId("비사용자");
        }else if(questionMember!=null) {        // 관리자 일대만 사용자 정보 포함
            memberDtoBuilder.id(questionMember.getId()).loginId(questionMember.getLoginId());
        }
        questionDtoBuilder.member(memberDtoBuilder.build());

        return questionDtoBuilder.build();
    }

    public static QuestionDto of(Question question, String url){

        QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder()
                .title(question.getTitle())
                .answerType(question.getAnswer())
                .questionType(question.getQuestionType())
                .create_at(question.getCreatedAt())
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
       LocalDateTime createdDate = question.getCreatedAt();

       QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder().id(question.getId())
               .status(question.getStatus()).title(question.getTitle())
               .question_content(question.getQuestion_content())
               .create_at(createdDate.withSecond(0).withNano(0));

       if(question.getMember()!=null){
           MemberDto memberDto = MemberDto.builder().id(question.getMember().getId()).loginId(question.getMember().getLoginId()).build();
           questionDtoBuilder.member(memberDto);
       }
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
