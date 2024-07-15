package com.team.RecipeRadar.domain.member.dto.rqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PasswordDuplicatedRequest {

    @Schema(description = "비밀번호",example = "asdASD12!@")
    private String password;
    @Schema(description = "비밀번호 재입력",example = "asdASD12!@")
    private String passwordRe;
}
