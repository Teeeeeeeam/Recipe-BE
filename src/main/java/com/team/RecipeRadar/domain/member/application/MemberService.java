package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.valid.PasswordStrengthDto;

import java.util.Map;

public interface MemberService {

    Member saveEntity(Member member);

    Member saveDto(MemberDto memberDto);

    Member findByLoginId(String loginId);

     Map<String, Boolean> LoginIdValid(String loginId);

    Map<String, Boolean> emailValid(String email);
    Map<String, Boolean> userNameValid(String username);
    Map<String, Boolean> checkPasswordStrength(String password);
    boolean ValidationOfSignUp(MemberDto memberDto,int code);
    Map<String, Boolean> duplicatePassword(String password,String passwordRe);

    Map<String,Boolean> nickNameValid(String nickName);
    Map<String, Boolean> verifyCode(String email,int code);

}
