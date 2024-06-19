package com.team.RecipeRadar.global.auth.application;

import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;

import java.util.Map;

public interface AuthService {

    void logout(Long id);

    Map<String, String> login(String loginId,String password);

    MemberInfoResponse accessTokenMemberInfo(String accessToken);

    String userToken(Long loginId, String password);
}
