package com.team.RecipeRadar.global.auth.application;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.RecipeRadar.global.auth.domain.RefreshToken;
import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
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
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRetrievalRepository accountRetrievalRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * 주어진 로그인 DTO를 사용하여 사용자를 인증하고, 성공적으로 인증되면 액세스 토큰과 리프레시 토큰을 생성하여 반환.
     * 만약 사용자가 존재하지 않거나 비밀번호가 일치하지 않는 경우에는 BadRequestException을 던짐.
     * @return 로그인 성공 시 액세스 토큰과 리프레시 토큰을 포함하는 맵
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
     * 일반 사용자 페이지 접근시 비밀번호 재 검증후 쿠키생성
     */
    @Override
    public String userToken(Long memberId, String password) {
        Member member = getMember(memberId);

        validPassword(password, member);

        return accountRetrievalRepository.save(AccountRetrieval.createAccount(member.getLoginId(),20)).getVerificationId();
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

    /* 비밀번호 검증 테스트 */
    private void validPassword(String password, Member member) {
        if (member.getLogin_type().equals("normal")) {
            boolean matches = passwordEncoder.matches(password, member.getPassword());
            if (!matches) {
                throw new InvalidIdException("비밀번호가 일치하지 않습니다.");
            }
        }
    }

    /* 사용자 정보 조회 */
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
    }

    /* 일반 사용자 인지 검증 메서드*/
    private static void validateNormalUser(Member member) {
        if (!member.getLogin_type().equals("normal"))
            throw new AccessDeniedException("일반 사용자만 가능합니다.");
    }
}
