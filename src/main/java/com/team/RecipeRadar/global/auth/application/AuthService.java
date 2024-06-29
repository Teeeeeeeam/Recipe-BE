package com.team.RecipeRadar.global.auth.application;

import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;

public interface AuthService {
    MemberInfoResponse accessTokenMemberInfo(String accessToken);
    String userToken(Long loginId, String password);
}
