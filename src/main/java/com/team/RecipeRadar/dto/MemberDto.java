package com.team.RecipeRadar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    Long id;

    @NotEmpty(message = "이름을 입력주세요")
    String username;
    
    @NotEmpty(message = "별명을 입력해주세요")
    String nickName;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    String password;

    @NotEmpty(message = "비밀번호를 다시한번 입력해주세요")
    String passwordRe;

    @NotEmpty(message = "아이디를 입력해주세요")
    String loginId;

    @NotEmpty(message = "이메일을 입력해주세요.")
    String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String roles;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDate join_date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String login_type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean verified;

    String code;
}
