package com.team.RecipeRadar.domain.questions.dto;

import com.team.RecipeRadar.domain.questions.domain.Answer;
import lombok.Data;

@Data
public class AnswerDto {

    private Long id;

    private String answerTitle;

    private String answerContent;

    private String answerAdminNickname;

    private AnswerDto(Long id, String answerTitle, String answerContent,String answer_admin_nickname) {
        this.id = id;
        this.answerTitle = answerTitle;
        this.answerContent = answerContent;
        this.answerAdminNickname = answer_admin_nickname;
    }

    public static AnswerDto fromDto(Answer answer){
        return new AnswerDto(answer.getId(),answer.getAnswerTitle(), answer.getAnswerContent(),answer.getAnswerAdminNickname());
    }
}
