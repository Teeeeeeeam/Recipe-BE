package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.RefreshToken;

public interface JwtAuthService {

    void logout(Long id);

    RefreshToken findRefreshToken(String token);

    void save(RefreshToken refreshToken);
}
