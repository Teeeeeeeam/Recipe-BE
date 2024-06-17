package com.team.RecipeRadar.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SignUpEmailVerificationRequest {

    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.(com|net)$", message = "올바른 이메일 형식이어야 합니다.")
    @Schema(description = "이메일",example = "test@naver.com")
    private String email;

    @NotNull
    @Min(value = 100000, message = "인증번호는 최소 6자리여야 합니다.")
    @Max(value = 999999, message = "인증번호는 최대 6자리여야 합니다.")
    @Schema(description = "이메일 인증번호",example = "123456")
    private Integer code;
}
