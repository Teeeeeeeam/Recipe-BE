package com.team.RecipeRadar.global.security.oauth2.provider;

import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Getter
public class Oauth2UrlProvider{

    @Value("${custom.redirect.kakao}")
    String kakaoRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String secretId;

    @Value("${custom.redirect.naver}")
    String naverRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    String naverSecretId;

    public String getRedirectUrl(String loginType) {
        String url = null;
        if (loginType.equals("kakao")){
            url ="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoClientId+"&redirect_uri="+kakaoRedirectUrl+"&prompt=login";
        } else if (loginType.equals("naver")) {
            SecureRandom secureRandom = new SecureRandom();
            String state = new BigInteger(130, secureRandom).toString();
            url ="https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+naverClientId+"&state="+state+"&redirect_uri="+naverRedirectUrl;
            log.info("url={}",url);
        } else throw new BadRequestException("오류");

        return url;
    }
}
