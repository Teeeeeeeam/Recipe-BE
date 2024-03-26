package com.team.RecipeRadar.global.jwt.Service;

import com.team.RecipeRadar.global.jwt.Entity.RefreshToken;

public interface JwtAuthService {

    void logout(Long id);

    RefreshToken findRefreshToken(String token);

    void save(RefreshToken refreshToken);
}
