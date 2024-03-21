package com.team.RecipeRadar.global.email.application;

public interface JoinEmailService {

    String generateVerification(String username);       // 이메일 인증 토큰 생성

    String getVerificationIdByUsername(String username);   // 유저네임으로 이메일 인증 토큰 얻기

    String getUsernameForVerificationId(String verificationToken);   // 이메일 인증 토큰에 해당하는 유저네임 얻기

}
