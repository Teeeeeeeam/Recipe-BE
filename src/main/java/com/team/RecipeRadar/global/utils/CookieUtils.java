package com.team.RecipeRadar.global.utils;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtils {

    private final AccountRetrievalRepository accountRetrievalRepository;

    public ResponseCookie createCookie(String cookieName, String value, int expiredTime){

        String cookieValue = value;

        cookieValue = encodeCookieValueIfNeeded(cookieName, value, cookieValue);

        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(expiredTime)
                .build();
    }

    public void validCookie(String cookieValue,String loginId){
        validStatCookie(cookieValue == null);
        String decodeCookie = new String(Base64.getDecoder().decode(cookieValue.getBytes()));
        AccountRetrieval accountRetrieval = accountRetrievalRepository.findByLoginIdAndVerificationId(loginId, decodeCookie);
        validStatCookie(decodeCookie == null || accountRetrieval ==null || !accountRetrieval.getExpireAt().isAfter(LocalDateTime.now()));
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

    private static String encodeCookieValueIfNeeded(String cookieName, String value, String cookieValue) {
        if(!cookieName.equals("RefreshToken")) {
            cookieValue = new String(Base64.getEncoder().encode(value.getBytes()));
        }
        return cookieValue;
    }
}
