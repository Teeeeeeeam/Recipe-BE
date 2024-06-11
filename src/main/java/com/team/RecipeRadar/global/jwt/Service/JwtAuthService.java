package com.team.RecipeRadar.global.jwt.Service;

import com.team.RecipeRadar.global.jwt.Entity.RefreshToken;
import com.team.RecipeRadar.global.jwt.controller.LoginRequest;
import com.team.RecipeRadar.global.jwt.dto.MemberInfoResponse;

import java.util.Map;

public interface JwtAuthService {

    void logout(Long id);

    void save(RefreshToken refreshToken);

    Map<String, String> login(LoginRequest loginDto);

    MemberInfoResponse accessTokenMemberInfo(String accessToken);
}
