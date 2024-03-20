package com.team.RecipeRadar.filter.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Entity.RefreshToken;
import com.team.RecipeRadar.Service.JwtAuthService;
import com.team.RecipeRadar.exception.ex.JwtTokenException;
import com.team.RecipeRadar.payload.ApiResponse;
import com.team.RecipeRadar.repository.JWTRefreshTokenRepository;
import com.team.RecipeRadar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    private final MemberRepository memberRepository;

    private static final int TOKEN_TIME = 1; //10분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME =1; // 7일
    private final JwtAuthService jwtAuthService;

    @Value("${security.token}")
    private String secret;

    /**
     * 엑세스 토큰을 만드는 메서드
     *
     * @param loginId
     * @return 새로운 엑세시 토큰을 만들어 String 타입으로 반환
     */

    public String generateAccessToken(String loginId) {

        Member member = memberRepository.findByLoginId(loginId);

        LocalDateTime now = LocalDateTime.now().plusMinutes(TOKEN_TIME);
        Date date = Timestamp.valueOf(now);
        System.out.println(date);
        String token = JWT.create()
                .withSubject("Token")
                .withExpiresAt(date)
                .withClaim("id", member.getId())
                .withClaim("loginId", member.getLoginId())
                .sign(Algorithm.HMAC512(secret));
        return token;
    }

    /**
<<<<<<< HEAD
=======

>>>>>>> 88c2cde0e44d1b184f39eb764e198d10a946f626
     * 리프레쉬 토큰을 생성해주는 메서드
     * @param loginId
     * @return
     */
    public String generateRefreshToken(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);

        LocalDateTime expirationDateTime = LocalDateTime.now().plusMonths(REFRESH_TOKEN_EXPIRATION_TIME);
        Date expirationDate = java.sql.Timestamp.valueOf(expirationDateTime);

        String refreshToken = JWT.create()
                .withSubject("RefreshToken")
                .withExpiresAt(expirationDate)
                .withClaim("id", member.getId())
                .withClaim("loginId", member.getLoginId())
                .sign(Algorithm.HMAC512(secret));

        RefreshToken token = RefreshToken.builder().member(member).refreshToken(refreshToken).tokenTIme(expirationDateTime).build();

        jwtAuthService.save(token);
        return refreshToken;
    }

    /**
     * JWT 토큰의 만료를 검증하는 메소드
     *
     * @param token
     * @return 만료되지않았다면 ture 만료되었으면  false
     */
    public Boolean TokenExpiration(String token) {

        DecodedJWT decodedJWT = JWT.decode(token);
        Date expiresAt = decodedJWT.getExpiresAt();
        if (expiresAt != null && expiresAt.before(new Date())) {
            return false;
        } else
            return true;
    }

    /**
     * 토큰을 검증하는 메서드
     *
     * @param token
     * @return
     */

    public String validateAccessToken(String token){

        try {
            String loginId = JWT.require(Algorithm.HMAC512(secret)).build()
                    .verify(token)
                    .getClaim("loginId")
                    .asString();

            return loginId;
        } catch (SignatureVerificationException e) {
            log.error("존재하지 않은 토큰 사용");
            throw new JwtTokenException("토큰이 존재하지 않습니다.");
        }

    }

    public String validateRefreshToken(String refreshToke){
        try{
            DecodedJWT decodedJWT = JWT.decode(refreshToke);

            String loginId = decodedJWT.getClaim("loginId").asString();
            RefreshToken refreshToken = jwtAuthService.findRefreshToken(refreshToke);

            Boolean isTokenTIme = TokenExpiration(refreshToke);

            if (loginId.equals(refreshToken.getMember().getLoginId())&&isTokenTIme){
                String token = generateAccessToken(refreshToken.getMember().getLoginId());
                return token;
            }else
                return null;

        }catch (Exception e){
          throw new JwtTokenException("잘못된 토큰 형식입니다.");
        }
    }


    private Member getMember(Authentication authentication) {
        String name = authentication.getName();

        Member member = memberRepository.findByLoginId(name);
        return member;
    }
}