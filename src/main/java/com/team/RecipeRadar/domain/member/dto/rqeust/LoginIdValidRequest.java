package com.team.RecipeRadar.domain.member.dto.rqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(name = "로그인 아이디 검증 Request")
public class LoginIdValidRequest {

    @NotEmpty(message = "아이디를 입력해주세요")
    @Schema(description = "아이디 조건검사 대문자나 소문자(5~16)",example = "exampleId")
    private String loginId;
}
