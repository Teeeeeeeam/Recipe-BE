package com.team.RecipeRadar.domain.member.dto.AccountRetrieval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {

    @Schema(description = "가입한 아이디", example = "testId")
    private String loginId;

    @Schema(description = "변경할 비밀번호", example = "asdASD123!@")
    private String password;

    @Schema(description = "변경할 비밀번호 재입력",example ="asdASD123!@")
    private String passwordRe;
}