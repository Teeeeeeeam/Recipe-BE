package com.team.RecipeRadar.domain.questions.dto;

import com.team.RecipeRadar.domain.questions.domain.AnswerType;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "문의 사항 작성 Request")
public class QuestionRequest {

    @Schema(hidden = true)
    private Long id;

    @Schema(example = "GENERAL_INQUIRY" ,description = "문의 사항 종류 일반문의(GENERAL_INQUIRY)와 계정 문의(ACCOUNT_INQUIRY)가 존재")
    private QuestionType questionType;

    @Schema(example = "문의 사항 제목")
    private String title;

    @Schema(example = "문의 사항 내용")
    private String question_content;

    @Schema(example = "EMAIL", description = "문의 사항 작성시 알림 받기 서비스")
    private AnswerType answer;

    @Schema(example = "keuye06380618@gmail.com", description = "알림 받을 이메일 주소")
    private String answer_email;

}
