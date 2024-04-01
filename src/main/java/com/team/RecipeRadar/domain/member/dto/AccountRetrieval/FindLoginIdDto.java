package com.team.RecipeRadar.domain.member.dto.AccountRetrieval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindLoginIdDto {

    @Schema(description = "가입할때 입력한 사용자 실명",example = "홍길동")
    String username;

    @Schema(description = "회원가입시 입력한 이메일 주소", example = "test@naver.com")
    String email;

    @Schema(description = "이메일 인증번호",example = "123456")
    String code;
}
