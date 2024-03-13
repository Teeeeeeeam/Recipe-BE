package com.team.RecipeRadar.security.oauth2;

import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.security.oauth2.provider.GoogleUserInfo;
import com.team.RecipeRadar.security.oauth2.provider.KakaoUserInfo;
import com.team.RecipeRadar.security.oauth2.provider.NaverUserInfo;
import com.team.RecipeRadar.security.oauth2.provider.Oauth2UserInfo;
import com.team.RecipeRadar.repository.MemberRepository;
import com.team.RecipeRadar.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2Service extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User user = super.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();
        log.info("aass={}",attributes);

        String requestId = userRequest.getClientRegistration().getRegistrationId();  // 요청한 oath2 사이트 회사명

        log.info("reee={}",requestId);
        Oauth2UserInfo oauth2UserInfo =null;
        Member member=null;

        if (requestId.equals("kakao")){
            oauth2UserInfo = new KakaoUserInfo(attributes);
            member = save(oauth2UserInfo.getId(), oauth2UserInfo.getName(), oauth2UserInfo.getEmail(), requestId);
        } else if (requestId.equals("google")) {
            oauth2UserInfo = new GoogleUserInfo(attributes);
            member = save(oauth2UserInfo.getId(), oauth2UserInfo.getName(), oauth2UserInfo.getEmail(), requestId);
        }

        return new PrincipalDetails(member);
    }

    /**
     * oauth2 로그인시에 사용되는 메소드 만약 이전의 로그인한 사용자가 있다면 email정보만 업데이트 
     * 만약 로그인한 정보가 없다면 자동으로 회원가입 진행
     * @return member 객체 반환
     */
    private Member save(String id, String name, String email,String requestId) {
        Member member = memberRepository.findByLoginId(id);
        if (member==null){
            Member user = Member.builder()
                    .loginId(id)
                    .username(name)
                    .email(email)
                    .login_type(requestId)
                    .join_date(LocalDate.now())
                    .nickName(name)
                    .verified(true).roles("ROLE_USER").build();
            Member save = memberRepository.save(user);
            return save;
        }else {
            member.setEmail(email);
            memberRepository.save(member);
        }
        return member;
    }

}
