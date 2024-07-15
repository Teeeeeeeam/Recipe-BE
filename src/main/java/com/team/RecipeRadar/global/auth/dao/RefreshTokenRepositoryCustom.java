package com.team.RecipeRadar.global.auth.dao;

import com.team.RecipeRadar.domain.member.dto.MemberDto;

public interface RefreshTokenRepositoryCustom {
    MemberDto existsByRefreshTokenAndMember(String refreshToken, String loginId);
}
