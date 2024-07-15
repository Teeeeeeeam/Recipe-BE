package com.team.RecipeRadar.global.auth.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.RecipeRadar.global.security.oauth2.application.impl.GoogleUserDisConnectServiceImpl;
import com.team.RecipeRadar.global.security.oauth2.application.impl.KakaoUserDisConnectServiceImpl;
import com.team.RecipeRadar.global.security.oauth2.application.impl.NaverUserDisConnectServiceImpl;
import com.team.RecipeRadar.global.security.oauth2.provider.Oauth2UrlProvider;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.auth.application.AuthService;
import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Import(SecurityTestConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean AuthService jwtAuthService;
    @MockBean CookieUtils cookieUtils;
    @MockBean KakaoUserDisConnectServiceImpl kakaoUserDisConnectService;
    @MockBean NaverUserDisConnectServiceImpl naverUserDisConnectService;
    @MockBean GoogleUserDisConnectServiceImpl googleUserDisConnectService;
    @MockBean Oauth2UrlProvider oauth2UrlProvider;
    @Autowired MockMvc mockMvc;

    
    @Test
    @DisplayName("엑세스 토큰 유저 정보 조회 API 테스트")
    void AccessToken_get_UserInfo() throws Exception {
        String fakeToken = "fakeToken";
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .loginId("loginId")
                .nickname("닉네임")
                .login_type("normal")
                .build();

        MemberInfoResponse of = MemberInfoResponse.of(memberDto);

        given(jwtAuthService.accessTokenMemberInfo(eq(fakeToken))).willReturn(of);

        mockMvc.perform(post("/api/userinfo")
                        .header("Authorization","Bearer"+fakeToken)
                )
                .andDo(print())
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nickName").value("닉네임"));
    }

    @Test
    @DisplayName("엑세스 토큰 유저 정보 조회시 401 예외 발생")
    void AccessToken_getInfo_ServerEx() throws Exception {
        String fakeToken = "fakeToken";
        given(jwtAuthService.accessTokenMemberInfo(eq(fakeToken))).willThrow(new JwtTokenException("토큰이 존재하지 않습니다."));

        mockMvc.perform(post("/api/userinfo")
                        .header("Authorization","Bearer"+fakeToken)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message").value("토큰이 존재하지 않습니다."));
    }
}