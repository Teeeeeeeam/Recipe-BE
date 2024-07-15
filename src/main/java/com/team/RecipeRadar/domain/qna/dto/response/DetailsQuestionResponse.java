package com.team.RecipeRadar.domain.qna.dto.response;

import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import lombok.Data;

@Data
public class DetailsQuestionResponse {

    private QuestionDto question;

    private String url;
}
