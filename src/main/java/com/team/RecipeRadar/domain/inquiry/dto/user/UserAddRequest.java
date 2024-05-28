package com.team.RecipeRadar.domain.inquiry.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class UserAddRequest {

    @Schema(description = "문의사항 제목", example = "문의 제목!")
    @NotEmpty(message = "문의사항 제목을 입력해주세요")
    private String inquiryTitle;

    @Schema(description = "문의사항 내용", example = "문의 내용!")
    @NotEmpty(message = "문의사항 내용을 입력해주세요")
    private String inquiryContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Schema(description = "문의사항 비밀번호", example = "123456")
    private String inquiryPassword;

}
