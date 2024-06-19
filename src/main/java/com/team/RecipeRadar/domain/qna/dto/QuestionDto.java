package com.team.RecipeRadar.domain.qna.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.qna.domain.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
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

    private String questionContent;

    private QuestionStatus status;

    private AnswerType answerType;

    private LocalDate createdAt;

    private LocalDate answeredAt;

    private String imgUrl;

    private String answerEmail;

    private MemberDto member;

    private AnswerDto answer;


    private QuestionDto(QuestionType questionType, String title, String questionContent, QuestionStatus status, AnswerType answer,String answerEmail, MemberDto memberDto) {
        this.questionType = questionType;
        this.title = title;
        this.questionContent = questionContent;
        this.status = status;
        this.answerType = answer;
        this.answerEmail =answerEmail;
        this.member = memberDto;
    }

    public static QuestionDto pageDto(Question question){

        Member questionMember = question.getMember();
        QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder()
                .id(question.getId())
                .title(question.getTitle())
                .createdAt(question.getCreatedAt().toLocalDate())
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
                .createdAt(question.getCreatedAt().toLocalDate())
                .questionContent(question.getQuestionContent())
                .status(question.getStatus());


        if (question.getMember()!=null){
            MemberDto memberDto = MemberDto.builder().id(question.getMember().getId()).nickname(question.getMember().getNickName()).loginId(question.getMember().getLoginId()).build();
            questionDtoBuilder.member(memberDto);
        }
        if (question.getAnswerEmail()!=null){
            questionDtoBuilder.answerEmail(question.getAnswerEmail());
        }
        if(url!=null){
            questionDtoBuilder.imgUrl(url);
        }

        return questionDtoBuilder.build();
    }

   public static QuestionDto of(Question question, Answer answer, String img_url){
       LocalDateTime createdDate = question.getCreatedAt();

       QuestionDtoBuilder questionDtoBuilder = QuestionDto.builder().id(question.getId())
               .status(question.getStatus()).title(question.getTitle())
               .questionContent(question.getQuestionContent())
               .createdAt(createdDate.toLocalDate());

       if(question.getMember()!=null){
           MemberDto memberDto = MemberDto.builder().id(question.getMember().getId()).loginId(question.getMember().getLoginId()).build();
           questionDtoBuilder.member(memberDto);
       }
       if(img_url!=null){
           questionDtoBuilder.imgUrl(img_url);
       }

       if(answer!=null) {
           questionDtoBuilder.answer(AnswerDto.fromDto(answer));
           questionDtoBuilder.answeredAt(createdDate.toLocalDate());
       }
       return questionDtoBuilder.build();
   }
}
