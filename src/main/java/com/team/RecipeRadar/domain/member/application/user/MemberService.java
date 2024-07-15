package com.team.RecipeRadar.domain.member.application.user;

import com.team.RecipeRadar.domain.member.dto.response.UserInfoResponse;

import java.util.Map;

public interface MemberService {
    Map<String, Boolean> checkPasswordStrength(String password);
    Map<String, Boolean> duplicatePassword(String password,String passwordRe);
    void deleteByLoginId(String loginId);
    UserInfoResponse getMembers(Long memberId);
    void updateNickName(String nickName,Long memberId);
    void deleteMember(Long memberId, boolean checkType);
    void updateEmail(String email,Integer code,Long memberId);
}
