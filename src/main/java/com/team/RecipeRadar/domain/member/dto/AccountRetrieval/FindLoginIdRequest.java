package com.team.RecipeRadar.domain.member.dto.AccountRetrieval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "아이디 찾기 Request")
public class FindLoginIdRequest {

    @Schema(description = "가입할때 입력한 사용자 실명",example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요")
    @Pattern(regexp = "^[가-힣]+.{1,}$",message = "이름을 정확이 입력해주세요")
    String username;

    @NotBlank(message = "이메일을 입력해주세요")
    @Schema(description = "회원가입시 입력한 이메일 주소", example = "test@naver.com")
    String email;

    @NotNull
    @Min(value = 100000, message = "인증번호는 최소 6자리여야 합니다.")
    @Schema(description = "이메일 인증번호",example = "123456")
    Integer code;
}
