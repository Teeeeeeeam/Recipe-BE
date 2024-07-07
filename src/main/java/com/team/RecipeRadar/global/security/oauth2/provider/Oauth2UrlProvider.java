package com.team.RecipeRadar.global.security.oauth2.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;

import static com.team.RecipeRadar.global.security.oauth2.provider.SocialType.*;

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

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${custom.redirect.google}")
    String googleRedirectUrl;

    public String getRedirectUrl(SocialType socialType) {
        String url = null;
        if (socialType.equals(kakao)){
            url ="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoClientId+"&redirect_uri="+kakaoRedirectUrl+"&prompt=login";
        } else if (socialType.equals(naver)) {
            SecureRandom secureRandom = new SecureRandom();
            String state = new BigInteger(130, secureRandom).toString();
            url ="https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+naverClientId+"&state="+state+"&redirect_uri="+naverRedirectUrl;
        } else if (socialType.equals(google)) {
            url="https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri="+ googleRedirectUrl+"&response_type=code&scope=email%20profile%20openid&access_type=offline";
        } else throw new IllegalStateException("오류");

        return url;
    }
}
