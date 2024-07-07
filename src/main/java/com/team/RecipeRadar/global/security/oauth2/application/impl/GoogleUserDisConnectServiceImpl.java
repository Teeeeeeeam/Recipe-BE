package com.team.RecipeRadar.global.security.oauth2.application.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("google")
public class GoogleUserDisConnectServiceImpl implements UserDisConnectService {

    private final ObjectMapper objectMapper;
    private final Oauth2UrlProvider oauth2UrlProvider;
    private final MemberService memberService;



    @Override
    public String getAccessToken(String auth2Code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("client_id", oauth2UrlProvider.getGoogleClientId());
        requestBody.add("client_secret", oauth2UrlProvider.getGoogleClientSecret());
        requestBody.add("redirect_uri", "https://localhost:8443/api/oauth2/unlink/google");
        requestBody.add("code", auth2Code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenUrl, requestEntity, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean disconnect(String accessToken) {
        try {
            String requestUrl = "https://oauth2.googleapis.com/revoke";
            String loginId = getUserNumber(accessToken);
            memberService.deleteByLoginId(loginId);

            String requestBody = "token=" + accessToken;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, request, String.class);
            if(response.getStatusCode().is2xxSuccessful()){
                return true;
            }
        }catch (Exception e){
            throw new ServerErrorException(e.getMessage());
        }
        return false;
    }

    private String getUserNumber(String accessToken) {
        try {
            String infoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    infoUrl,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                return jsonNode.get("sub").asText();
            } else
                throw new ServerErrorException("정보 가저오기 실패");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(e.getMessage());
        }
    }
}
