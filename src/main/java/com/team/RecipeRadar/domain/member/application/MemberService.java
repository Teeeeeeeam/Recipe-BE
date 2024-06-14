package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dto.MemberDto;

import java.util.Map;

public interface MemberService {
    void joinMember(MemberDto memberDto);
    Map<String, Boolean> LoginIdValid(String loginId);
    Map<String, Boolean> emailValid(String email);
    Map<String, Boolean> checkPasswordStrength(String password);
    boolean ValidationOfSignUp(MemberDto memberDto);
    Map<String, Boolean> duplicatePassword(String password,String passwordRe);
    void nickNameValid(String nickName);
    Map<String, Boolean> verifyCode(String email,int code);
    void deleteMember(String loginId);
    Map<String,String> ValidationErrorMessage(MemberDto memberDto);

    void emailValidCon(String email);
}
