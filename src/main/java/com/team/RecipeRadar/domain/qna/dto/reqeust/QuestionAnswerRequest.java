package com.team.RecipeRadar.domain.qna.dto.reqeust;

import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(name = "문의 사항 답변 Request")
public class QuestionAnswerRequest {

    @Schema(example = "답변 제목")
    @NotEmpty(message = "답변 제목을 작성해 주세요")
    private String answerTitle;
    
    @Schema(example = "답변 내용")
    @NotEmpty(message = "답변 내용을 작성해 주세요")
    private String answerContent;

    @Schema(example = "COMPLETED")
    QuestionStatus questionStatus;
}
