package com.team.RecipeRadar.global.security.jwt.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.team.RecipeRadar.global.auth.domain.RefreshToken;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${token.access}")
    private int ACCESS_TOKEN_TINE;

    @Value("${token.refresh}")
    private int REFRESH_TOKEN_TINE;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository jwtRefreshTokenRepository;

    private final String CLAM_ID ="id";
    private final String LOGIN_ID ="loginId";
    private final String NICKNAME ="nickName";
    private final String LOGIN_TYPE ="loginType";

    @Value("${security.token}")
    private String secret;

    /**
     * 엑세스 토큰을 생성 메서드
     */
    public String generateAccessToken(String loginId) {
        Member member = getMember(loginId);

        LocalDateTime now = LocalDateTime.now().plusMinutes(ACCESS_TOKEN_TINE);
        return creatToken("Token",  Timestamp.valueOf(now), member);
    }

    /**
     * 리프레쉬 토큰을 생성해주는 메서드
     */
    public String generateRefreshToken(String loginId) {
        Member member = getMember(loginId);
        RefreshToken refreshTokenMember = jwtRefreshTokenRepository.findByMemberId(member.getId());

        LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(REFRESH_TOKEN_TINE);
        Date expired =  Timestamp.valueOf(expirationDateTime);

        String refreshToken ="";

        if (refreshTokenMember==null) {
            refreshToken = creatToken("RefreshToken", expired, member);
            jwtRefreshTokenRepository.save(RefreshToken.createRefreshToken(member,refreshToken,expirationDateTime));
        }else {
            refreshTokenMember.update(refreshTokenMember.getRefreshToken());    // 로그인을 두번 요청되었을때를 대비해 업데이트 작성
        }

        return refreshToken;
    }

    /**
     * JWT 토큰의 만료 시간을 검증하는 메소드
     */
    public Boolean TokenExpiration(String token) {
        Date expired = JWT.decode(token).getExpiresAt();
        return expired != null && expired.before(new Date());
    }

    /**
     * 토큰을 검증하는 메서드
     */
    public String validateAccessToken(String token){
        try {
            return verifyToken(token);
        } catch (SignatureVerificationException e) {
            log.error("존재하지 않은 토큰 사용");
            throw new JwtTokenException("토큰이 존재하지 않습니다.");
        }
    }

    /**
     * 리프레쉬 토큰을 검증하는 메서드
     * 만료되지않았거나 토큰이 DB에 존재할때 TRUE
     */
    public String validateRefreshToken(String refreshToken){
        String loginId = verifyToken(refreshToken);

        Boolean existsByRefreshToken = jwtRefreshTokenRepository.existsByRefreshTokenAndMemberLoginId(refreshToken, loginId);
        Boolean isTokenTIme = TokenExpiration(refreshToken);

        if (!existsByRefreshToken && !isTokenTIme) throw new JwtTokenException("사용할수 없는 토큰 입니다.");
        return generateAccessToken(loginId);
    }

    /* 토큰의 loginId 값을 조회 */
    private String verifyToken(String tokenName) {
        return JWT.require(Algorithm.HMAC512(secret)).build()
                .verify(tokenName)
                .getClaim(LOGIN_ID)
                .asString();
    }

    /* 토큰 생성 */
    private String creatToken(String tokenName, Date expirationDate, Member member) {
        return  JWT.create()
                .withSubject(tokenName)
                .withExpiresAt(expirationDate)
                .withClaim(CLAM_ID, member.getId())
                .withClaim(LOGIN_ID, member.getLoginId())
                .withClaim(NICKNAME, member.getNickName())
                .withClaim(LOGIN_TYPE, member.getLogin_type())
                .sign(Algorithm.HMAC512(secret));
    }

    /* 사용자 정보 조회 */
    private Member getMember(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }
}