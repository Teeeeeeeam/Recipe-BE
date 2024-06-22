package com.team.RecipeRadar.domain.member.dto.rqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(name = "이메일 검증 Request")
public class EmailValidRequest {

    @Schema(description = "이메일 검증", example = "자신이메일@naver.com")
    @NotEmpty(message = "이메일을 입력해주세요")
    private String email;
}
