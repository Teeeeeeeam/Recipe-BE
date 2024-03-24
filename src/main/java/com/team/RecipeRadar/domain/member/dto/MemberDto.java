package com.team.RecipeRadar.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.domain.Member;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String username;
    
    @NotEmpty(message = "별명을 입력해주세요")
    String nickName;


    @NotEmpty(message = "비밀번호를 입력해주세요")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String password;


    @NotEmpty(message = "비밀번호를 다시한번 입력해주세요")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String passwordRe;

    @NotEmpty(message = "아이디를 입력해주세요")
    String loginId;


    @NotEmpty(message = "이메일을 입력해주세요.")
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
