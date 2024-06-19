package com.team.RecipeRadar.global.jwt.Service;

import com.team.RecipeRadar.global.jwt.dto.MemberInfoResponse;

import java.util.Map;

public interface JwtAuthService {

    void logout(Long id);

    Map<String, String> login(String loginId,String password);

    MemberInfoResponse accessTokenMemberInfo(String accessToken);
}
