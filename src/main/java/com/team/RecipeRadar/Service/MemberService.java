package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.dto.MemberDto;

import java.util.Map;

public interface MemberService {

    Member saveEntity(Member member);

    Member saveDto(MemberDto memberDto);

    Member findByLoginId(String username);

     Map<String, Boolean> LoginIdValid(MemberDto memberDto);

    Map<String, Boolean> emailValid(MemberDto memberDto);
    Map<String, Boolean> userNameValid(MemberDto memberDto);
    Map<String, Boolean> checkPasswordStrength(MemberDto memberDto);
    boolean ValidationOfSignUp(MemberDto memberDto,String code);
    Map<String, Boolean> duplicatePassword(MemberDto memberDto);

    Map<String,Boolean> nickNameValid(MemberDto memberDto);
    Map<String, Boolean> verifyCode(String code);


}
