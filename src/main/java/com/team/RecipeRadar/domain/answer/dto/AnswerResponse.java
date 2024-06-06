package com.team.RecipeRadar.domain.answer.dto;

import com.team.RecipeRadar.domain.answer.domain.Answer;
import lombok.Getter;

@Getter
public class AnswerResponse {
    private final String answerContent;

    public AnswerResponse(Answer answer) {
        this.answerContent = answer.getAnswerContent();
    }
}
