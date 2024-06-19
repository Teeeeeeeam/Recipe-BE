package com.team.RecipeRadar.global.security.oauth2.application.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.application.user.MemberService;
import com.team.RecipeRadar.global.security.oauth2.application.UserDisConnectService;
import com.team.RecipeRadar.global.security.oauth2.provider.Oauth2UrlProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerErrorException;

import java.math.BigInteger;
import java.security.SecureRandom;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("naver")
public class NaverUserDisConnectServiceImpl implements UserDisConnectService {

    private final ObjectMapper objectMapper;
    private final Oauth2UrlProvider oauth2UrlProvider;
    private final MemberService memberService;

    /*
    엑세스 토큰 발급 로직
     */
    @Override
    public String getAccessToken(String auth2Code) {
        try {
            String tokenUrl = "https://nid.naver.com/oauth2.0/token";

            SecureRandom secureRandom = new SecureRandom();
            String state = new BigInteger(130, secureRandom).toString();
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("client_id", oauth2UrlProvider.getNaverClientId());
            requestBody.add("client_secret", oauth2UrlProvider.getNaverSecretId());
            requestBody.add("state",state);
            requestBody.add("code", auth2Code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenUrl, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                String accessToken = jsonNode.get("access_token").asText();
                return accessToken;
            } else {
                log.error("엑세스 토큰 가져오기 실패: " + responseEntity.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     회원 탈퇴 로직
     */
    @Override
    public Boolean disconnect(String accessToken) {
        try {
            String requestUrl = "https://nid.naver.com/oauth2.0/token";
            String loginId = getUserNumber(accessToken);

            memberService.deleteByLoginId(loginId);
            
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type","delete ");
            requestBody.add("client_id",oauth2UrlProvider.getNaverClientId());
            requestBody.add("client_secret",oauth2UrlProvider.getNaverSecretId());
            requestBody.add("access_token",accessToken);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //oauth2 리소스 서버에서 토큰의 값을 이용해 사용자 정보 가져오기
    private String getUserNumber(String accessToken){
        try {
            String request= "https://openapi.naver.com/v1/nid/me";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    request,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()){;
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                String naverId = jsonNode.get("response").get("id").asText();
                return naverId;
            }else
                throw new ServerErrorException("정보 가저오기 실패");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
