package com.team.RecipeRadar.global.jwt.controller;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginDto {

    @NotEmpty(message = "아이디를 입력해주세요")
    private String loginId;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String password;
}
