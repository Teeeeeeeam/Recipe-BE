package com.team.RecipeRadar.global.jwt.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginDto {

    @NotEmpty(message = "아이디를 입력해주세요")
    @Schema(description = "로그인 아이디", example = "user1234")
    private String loginId;

    @Schema(description = "로그인 비밀번호", example = "asdASD12!@")
    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String password;
}
