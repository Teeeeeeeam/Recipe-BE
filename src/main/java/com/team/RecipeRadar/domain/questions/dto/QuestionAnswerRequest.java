package com.team.RecipeRadar.domain.questions.dto;

import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "문의 사항 답변 Request")
public class QuestionAnswerRequest {

    @Schema(example = "답변 제목")
    private String answerTitle;
    
    @Schema(example = "답변 내용")
    private String answerContent;

    @Schema(example = "COMPLETED")
    QuestionStatus questionStatus;
}
