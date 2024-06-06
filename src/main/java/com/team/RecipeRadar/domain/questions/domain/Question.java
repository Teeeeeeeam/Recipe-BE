package com.team.RecipeRadar.domain.questions.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;          //문의 유형

    private String title;  // 질문 제목

    @Column(length = 999)
    private String question_content;    //문의 내용

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;      //질문 알림을 받을 상태

    private String answer_email;     // 이메일 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
