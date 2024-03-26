package com.team.RecipeRadar.Service.impl;

import com.team.RecipeRadar.global.jwt.Entity.RefreshToken;
import com.team.RecipeRadar.global.jwt.Service.JwtAuthService;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtAuthServiceImpl implements JwtAuthService {

    private final JWTRefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(Long id) {
        RefreshToken byMemberId = refreshTokenRepository.findByMemberId(id);
        if (byMemberId!=null) {
            refreshTokenRepository.DeleteByMemberId(id);
        }else
            throw new JwtTokenException("해당 회원은 이미 로그아웃 했습니다.");
    }

    @Override
    public RefreshToken findRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByMemberLoginId(token);
        if (refreshToken==null)throw new JwtTokenException("토큰이 존재하지 않습니다.");
        return refreshToken;
    }

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }


}
