package com.team.RecipeRadar.global.jwt.Service;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.NoSuchErrorType;
import com.team.RecipeRadar.global.jwt.Entity.RefreshToken;
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
     * @return 로그인 성공 시 액세스 토큰과 리프레시 토큰을 포함하는 맵
     * @throws BadRequestException 로그인 실패 시 발생하는 예외
     */
    public Map<String, String> login(String loginId, String password) {
        Map<String, String> result = new LinkedHashMap<>();

        // 회원 정보 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new AccessDeniedException("아이디 및 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 일치 여부 확인
        boolean matches = passwordEncoder.matches(password, member.getPassword());
        if (!matches) {
            throw new AccessDeniedException("아이디 및 비밀번호가 일치하지 않습니다.");
        }

        // 인증 처리
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginId, password));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(member.getLoginId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getLoginId());

        // 결과 반환
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    /**
     * 엑세스 토큰을 통해 사용자의 정보를 조회한다.
     * @param accessToken   헤더로 요청된 accessToken값
     * @return MemberInfoResponse 객체를 반환
     */
    @Override
    public MemberInfoResponse accessTokenMemberInfo(String accessToken) {
        String loginId = jwtProvider.validateAccessToken(accessToken);
        Member member = memberRepository.findByLoginId(loginId);
        if(member!=null){
            return MemberInfoResponse.of(MemberDto.from(member));
        }else
            throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);
    }

    /**
     * 로그아웃 메서드
     */
    @Override
    public void logout(Long id) {
        RefreshToken byMemberId = refreshTokenRepository.findByMemberId(id);
        if (byMemberId != null) {
            refreshTokenRepository.DeleteByMemberId(id);
        } else
            throw new IllegalStateException("해당 회원은 이미 로그아웃 했습니다.");
    }
}
