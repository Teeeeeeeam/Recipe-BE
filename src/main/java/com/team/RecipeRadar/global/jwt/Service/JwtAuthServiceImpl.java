package com.team.RecipeRadar.global.jwt.Service;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.Entity.RefreshToken;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.jwt.controller.LoginRequest;
import com.team.RecipeRadar.global.jwt.dto.MemberInfoResponse;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JwtAuthServiceImpl implements JwtAuthService {

    private final JWTRefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * 주어진 로그인 DTO를 사용하여 사용자를 인증하고, 성공적으로 인증되면 액세스 토큰과 리프레시 토큰을 생성하여 반환.
     * 만약 사용자가 존재하지 않거나 비밀번호가 일치하지 않는 경우에는 BadRequestException을 던짐.
     * @param loginDto 로그인에 사용될 DTO 객체
     * @return 로그인 성공 시 액세스 토큰과 리프레시 토큰을 포함하는 맵
     * @throws BadRequestException 로그인 실패 시 발생하는 예외
     */
    public Map<String, String> login(LoginRequest loginDto) {
        Map<String, String> result = new LinkedHashMap<>();

        String loginId = loginDto.getLoginId();
        String password = loginDto.getPassword();

        Member byLoginId = memberRepository.findByLoginId(loginId);

        if (byLoginId != null) {
            boolean matches = passwordEncoder.matches(password, byLoginId.getPassword());
            if (matches) {
                Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginId, password));
                SecurityContextHolder.getContext().setAuthentication(authenticate);
                String accessToken = jwtProvider.generateAccessToken(byLoginId.getLoginId());
                String refreshToken = jwtProvider.generateRefreshToken(byLoginId.getLoginId());
                result.put("accessToken", accessToken);
                result.put("refreshToken", refreshToken);
                return result;
            }
            throw new AccessDeniedException("로그인 실패");
        } else {
            throw new AccessDeniedException("로그인 실패");
        }
    }

    /**
     * 엑세스 토큰을 통해 사용자의 정보를 조회한다.
     * @param accessToken   헤더로 요청된 accessToken값
     * @return MemberInfoResponse 객체를 반환
     */
    @Override
    public MemberInfoResponse accessTokenMemberInfo(String accessToken) {
        String loginId = jwtProvider.validateAccessToken(accessToken);
        Member byLoginId = memberRepository.findByLoginId(loginId);
        if(byLoginId!=null){
            return MemberInfoResponse.of(MemberDto.from(byLoginId));
        }else
            throw new NoSuchElementException("사용자를 찾을 수 없습니다.");
    }

    @Override
    public void logout(Long id) {
        RefreshToken byMemberId = refreshTokenRepository.findByMemberId(id);
        if (byMemberId != null) {
            refreshTokenRepository.DeleteByMemberId(id);
        } else
            throw new JwtTokenException("해당 회원은 이미 로그아웃 했습니다.");
    }


    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }
}
