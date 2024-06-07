package com.team.RecipeRadar.domain.questions.dto;

import com.team.RecipeRadar.domain.questions.domain.Answer;
import lombok.Data;

@Data
public class AnswerDto {

    private Long id;

    private String answer_title;

    private String answer_content;

    private String answer_admin_nickname;

    private AnswerDto(Long id, String answerTitle, String answerContent,String answer_admin_nickname) {
        this.id = id;
        this.answer_title = answerTitle;
        this.answer_content = answerContent;
        this.answer_admin_nickname = answer_admin_nickname;
    }

    public static AnswerDto fromDto(Answer answer){
        return new AnswerDto(answer.getId(),answer.getAnswerTitle(), answer.getAnswerContent(),answer.getAnswerAdminNickname());
    }
}
