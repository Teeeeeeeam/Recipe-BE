package com.team.RecipeRadar.domain.userInfo.utils;

import com.team.RecipeRadar.domain.member.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    private final AccountRetrievalRepository accountRetrievalRepository;

    public ResponseCookie createCookie(String cookieName, String value, int expiredTime){

        String userEncodeToken = new String(Base64.getEncoder().encode(value.getBytes()));

        ResponseCookie responseCookie = ResponseCookie.from(cookieName, userEncodeToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(expiredTime)
                .build();
        return responseCookie;
    }

    public void validCookie(String cookieValue,String loginId){
        validStatCookie(cookieValue == null);
        String decodeCookie = new String(Base64.getDecoder().decode(cookieValue.getBytes()));
        boolean existCookie = accountRetrievalRepository.existsByLoginIdAndVerificationId(loginId, decodeCookie);
        validStatCookie(decodeCookie == null || !existCookie);
    }

    private static void validStatCookie(boolean cookieValue) {
        if (cookieValue) throw new UnauthorizedException("올바르지 않은 접근입니다.");
    }

    public ResponseCookie deleteCookie(String cookieName){
        return ResponseCookie.from(cookieName,null)
                .secure(true)
                .httpOnly(true)
                .sameSite("None")
                .maxAge(0).path("/").build();
    }
}
