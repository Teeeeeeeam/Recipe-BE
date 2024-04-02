package com.team.RecipeRadar.domain.member.dto.AccountRetrieval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordDto {

    @Schema(description = "가입한 사용자 실명",example = "홍길동")
    private String username;
    
    @Schema(description = "가입한 사용자 아이디",example = "testId")
    private String loginId;

    @Schema(description = "가입한 사용자 이메일",example = "test@naver.com")
    private String email;
    
    @Schema(description = "전송된 이메일 인증번호",example = "123456")
    private String code;
}
