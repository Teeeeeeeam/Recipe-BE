package com.team.RecipeRadar.domain.questions.domain;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
public class Question extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;          //문의 유형

    private String title;  // 질문 제목

    @Column(length = 999)
    private String questionContent;    //문의 내용

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;      //질문 알림을 받을 상태

    private String answerEmail;     // 이메일 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL,orphanRemoval = true)
    List<UploadFile> uploadFiles = new ArrayList<>();

    //질문 상태 업데이트
    public void updateStatus(QuestionStatus questionStatus){
        this.status = questionStatus;
    }

    public static Question createQuestion(String title ,String questionContent,AnswerType answer,String answerEmail,QuestionType questionType){
        return Question.builder().title(title).questionContent(questionContent).answer(answer).status(QuestionStatus.PENDING).answerEmail(answerEmail).questionType(questionType).build();
    }
}
