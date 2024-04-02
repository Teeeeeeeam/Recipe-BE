package com.team.RecipeRadar.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    @Schema(nullable = true,hidden = true)
    Long id;


    @NotEmpty(message = "이름을 입력주세요")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "사용자 실명",example = "홍길동")
    String username;
    
    @NotEmpty(message = "별명을 입력해주세요")
    @Schema(description = "사용자의 별명",example = "나만냉")
    String nickName;


    @NotEmpty(message = "비밀번호를 입력해주세요")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "비밀번호",example = "asdASD12!@")
    String password;


    @NotEmpty(message = "비밀번호를 다시한번 입력해주세요")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "비밀번호 재입력",example = "asdASD12!@")
    String passwordRe;

    @NotEmpty(message = "아이디를 입력해주세요")
    @Schema(description = "로그인 아이디",example = "exampleId")
    String loginId;


    @NotEmpty(message = "이메일을 입력해주세요.")
    @Schema(description = "이메일",example = "test@naver.com")
    String email;

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String roles;

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate join_date;

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String login_type;

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean verified;

    @JsonIgnore
    String code;

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .loginId(loginId)
                .email(email)
                .join_date(join_date)
                .nickName(nickName).build();
    }

}
