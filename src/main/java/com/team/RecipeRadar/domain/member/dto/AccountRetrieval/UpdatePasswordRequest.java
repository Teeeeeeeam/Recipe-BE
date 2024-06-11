package com.team.RecipeRadar.domain.member.dto.AccountRetrieval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "비밀번호 변경 Reqeust")
public class UpdatePasswordRequest {

    @Schema(description = "가입한 아이디", example = "testId")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,16}$", message = "올바른 아이디를 입력해주세요")
    private String loginId;

    @Schema(description = "변경할 비밀번호", example = "asdASD123!@")
    @Pattern(regexp = "^(?=.*[`~!@#$%^&*()_+])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$",message = "사용할수 없는 비밀번호 입니다.")
    private String password;

    @Schema(description = "변경할 비밀번호 재입력",example ="asdASD123!@")
    @Pattern(regexp = "^(?=.*[`~!@#$%^&*()_+])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$",message = "사용할수 없는 비밀번호 입니다.")
    private String passwordRe;
}