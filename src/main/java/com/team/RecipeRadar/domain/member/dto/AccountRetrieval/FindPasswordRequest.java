package com.team.RecipeRadar.domain.member.dto.AccountRetrieval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "비밀번호 찾기 Request")
public class FindPasswordRequest {

    @Schema(description = "가입한 사용자 실명",example = "홍길동")
    @Pattern(regexp = "^[가-힣]+.{1,}$",message = "이름을 정확이 입력해주세요")
    private String username;
    
    @Schema(description = "가입한 사용자 아이디",example = "testId")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,16}$", message = "올바른 아이디를 입력해주세요")
    private String loginId;

    @Schema(description = "가입한 사용자 이메일",example = "test@naver.com")
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotNull
    @Min(value = 100000, message = "인증번호는 최소 6자리여야 합니다.")
    @Schema(description = "전송된 이메일 인증번호",example = "123456")
    private int code;

}
