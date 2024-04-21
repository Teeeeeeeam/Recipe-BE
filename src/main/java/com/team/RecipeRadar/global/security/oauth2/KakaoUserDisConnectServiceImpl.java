package com.team.RecipeRadar.global.security.oauth2;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
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
@Qualifier("kakao")
public class KakaoUserDisConnectServiceImpl implements UserDisConnectService{

    private final ObjectMapper objectMapper;
    private final Oauth2UrlProvider oauth2UrlProvider;
    private final MemberService memberService;

    @Override
    public String getAccessToken(String auth2Code) {
        try {
            String tokenUrl = "https://kauth.kakao.com/oauth/token";

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("client_id", oauth2UrlProvider.getKakaoClientId());
            requestBody.add("client_secret", oauth2UrlProvider.getSecretId());
            requestBody.add("redirect_uri", oauth2UrlProvider.getKakaoRedirectUrl());
            requestBody.add("code", auth2Code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenUrl, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("aaasdasda={}",responseEntity.getBody());

                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                log.info("js={}",jsonNode);
                String accessToken = jsonNode.get("access_token").asText();
                return accessToken;
            } else {
                log.error("Failed to get access token. Response: " + responseEntity.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean disconnect(String accessToken) {
        try {

            String requestUrl = "https://kapi.kakao.com/v1/user/unlink";
            String memberId = getUserNumber(accessToken);
            log.info("member={}",memberId);

            MemberDto memberDto = memberService.findByLoginId(memberId);

            memberService.deleteMember(memberDto.getId());

            log.info("사용자 정보={}",memberDto);
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

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

    private String getUserNumber(String accessToken){

        try {
            String request= "https://kapi.kakao.com/v1/user/access_token_info";

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
                String member_id = jsonNode.get("id").asText();
                return member_id;
            }else
                throw new ServerErrorException("정보 가저오기 실패");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
