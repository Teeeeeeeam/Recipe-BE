package com.team.RecipeRadar.domain.member.dto.rqeust;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Schema(name = "회원가입 Request")
public class JoinRequest {

    @NotEmpty(message = "이름을 입력주세요")
    @Pattern(regexp = "^[가-힣]+.{1,}$",message = "이름을 정확이 입력해주세요")
    @Schema(description = "사용자 실명",example = "홍길동")
    private String username;

    @NotEmpty(message = "별명을 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{4,}$",message = "사용할수 없는 별명입니다.")
    @Schema(description = "사용자의 별명",example = "나만냉")
    private String nickname;

    @NotEmpty(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,16}$", message = "올바른 아이디를 입력해주세요")
    @Schema(description = "로그인 아이디",example = "exampleId")
    private String loginId;

    @NotEmpty(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.(com|net)$", message = "올바른 이메일 형식이어야 합니다.")
    @Schema(description = "이메일",example = "test@naver.com")
    private String email;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[`~!@#$%^&*()_+])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$",message = "사용할수 없는 비밀번호 입니다.")
    @Schema(description = "비밀번호",example = "asdASD12!@")
    private String password;

    @NotEmpty(message = "비밀번호를 다시한번 입력해주세요")
    @Schema(description = "비밀번호 재입력",example = "asdASD12!@")
    private String passwordRe;

    @NotNull
    @Min(value = 100000, message = "인증번호는 최소 6자리여야 합니다.")
    @Max(value = 999999, message = "인증번호는 최대 6자리여야 합니다.")
    @Schema(description = "이메일 인증번호",example = "123456")
    Integer code;

    public static MemberDto fromDto(JoinRequest joinRequest){
        return MemberDto.builder()
                .username(joinRequest.getUsername())
                .nickname(joinRequest.getNickname())
                .password(joinRequest.getPassword())
                .passwordRe(joinRequest.getPasswordRe())
                .loginId(joinRequest.getLoginId())
                .code(joinRequest.getCode())
                .email(joinRequest.getEmail()).build();
    }
}
