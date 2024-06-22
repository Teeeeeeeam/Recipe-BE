package com.team.RecipeRadar.domain.member.application.user;

import com.team.RecipeRadar.domain.member.dto.MemberDto;

import java.util.Map;

public interface SinUpService {

    void joinMember(MemberDto memberDto);

    boolean ValidationOfSignUp(MemberDto memberDto);
    Map<String, Boolean> LoginIdValid(String loginId);

    Map<String, Boolean> emailValid(String email);

    void nickNameValid(String nickName);

    Map<String,String> ValidationErrorMessage(MemberDto memberDto);
}
