package com.team.RecipeRadar.domain.member.dto.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordStrengthRequest {

    @Schema(description = "비밀번호 강력도 검사",example = "asdASD12!@")
    private String password;
}
