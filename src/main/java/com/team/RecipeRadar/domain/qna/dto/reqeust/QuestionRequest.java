package com.team.RecipeRadar.domain.qna.dto.reqeust;

import com.team.RecipeRadar.domain.qna.domain.AnswerType;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Schema(name = "문의 사항 작성 Request")
public class QuestionRequest {

    @Schema(hidden = true)
    private Long id;

    @Schema(example = "GENERAL_INQUIRY" ,description = "문의 사항 종류 일반문의(GENERAL_INQUIRY)와 계정 문의(ACCOUNT_INQUIRY)가 존재")
    @NotNull(message = "문의사항 종류를 선택해주세요")
    private QuestionType questionType;

    @Schema(example = "문의 사항 제목")
    @NotEmpty(message = "문의사항 제목을 입력해주세요")
    private String title;

    @Schema(example = "문의 사항 내용")
    @NotEmpty(message = "문의사항 종류를 선택해주세요")
    private String questionContent;

    @Schema(example = "EMAIL", description = "문의 사항 작성시 알림 받기 서비스")
    @NotNull(message = "알림 서비스를 선택해주세요")
    private AnswerType answer;

    @Schema(example = "keuye06380618@gmail.com", description = "알림 받을 이메일 주소")
    @NotEmpty(message = "알림 받을 이메일 주소를 입력해주세요")
    private String answerEmail;

}
