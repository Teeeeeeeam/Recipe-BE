package com.team.RecipeRadar.domain.questions.dto;

import com.team.RecipeRadar.domain.questions.domain.AnswerType;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import lombok.Data;

@Data
public class QuestionRequest {

    private Long id;

    private QuestionType questionType;

    private String title;

    private String question_content;

    private AnswerType answer;

    private String answer_email;

    private Long memberId;
}
