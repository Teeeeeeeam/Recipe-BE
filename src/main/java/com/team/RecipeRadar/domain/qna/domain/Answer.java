package com.team.RecipeRadar.domain.qna.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "question_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "question,member")
public class Answer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    private String answerTitle;

    @Column(length = 1000)
    private String answerContent;

    private String answerAdminNickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public static Answer createAanswer(String answerTitle,String answerContent,String answerAdminNickname, Question question){
        return Answer.builder().answerTitle(answerTitle).answerContent(answerContent).answerAdminNickname(answerAdminNickname).question(question).build();
    }
}
