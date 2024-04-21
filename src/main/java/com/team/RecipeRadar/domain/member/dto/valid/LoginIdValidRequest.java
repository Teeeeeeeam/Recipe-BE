package com.team.RecipeRadar.domain.member.dto.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginIdValidRequest {

    @NotEmpty(message = "아이디를 입력해주세요")
    @Schema(description = "아이디 조건검사 대문자나 소문자(5~16)",example = "exampleId")
    private String loginId;
}
