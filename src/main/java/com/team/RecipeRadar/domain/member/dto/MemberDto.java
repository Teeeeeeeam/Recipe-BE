package com.team.RecipeRadar.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDto {

    @Schema(nullable = true,hidden = true)
    private Long id;

    private String username;

    private String nickname;

    private String password;

    private String passwordRe;

    private String loginId;

    private String email;

    @JsonIgnore
    private String roles;

    @JsonIgnore
    private LocalDate join_date;

    @JsonIgnore
    private String login_type;

    @JsonIgnore
    private boolean verified;

    @JsonIgnore
    private Integer code;

    public static Member toEntity(MemberDto memberDto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .id(memberDto.getId())
                .loginId(memberDto.loginId)
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .email(memberDto.email)
                .createAt(LocalDate.now())
                .nickName(memberDto.nickname)
                .username(memberDto.getUsername())
                .login_type("normal")
                .roles("ROLE_USER")
                .verified(true)
                .build();
    }

    private MemberDto(Long memberId, String loginId, String email, String username, String nickname, LocalDate join_date) {
        this.id = memberId;
        this.loginId = loginId;
        this.email = email;
        this.username = username;
        this.nickname =nickname;
        this.join_date = join_date;
    }

    public static MemberDto from(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .username(member.getUsername())
                .nickname(member.getNickName())
                .password(member.getPassword())
                .email(member.getEmail())
                .loginId(member.getLoginId())
                .roles(member.getRoles())
                .login_type(member.getLogin_type()).build();
    }

    public static MemberDto of(Long memberId, String loginId, String email, String username, String nickname, LocalDate join_date){
        return new MemberDto(memberId, loginId, email, username, nickname, join_date);
    }
}
