package com.team.RecipeRadar.domain.questions.dto;

import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import lombok.Data;

@Data
public class QuestionAnswerRequest {


    private String answer_title;
    private String answer_content;
    QuestionStatus questionStatus;
}
