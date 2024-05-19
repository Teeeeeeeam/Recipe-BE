package com.team.RecipeRadar.global.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.JwtTokenException;
import com.team.RecipeRadar.global.jwt.Service.JwtAuthService;
import com.team.RecipeRadar.global.jwt.dto.MemberInfoResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean JwtAuthService jwtAuthService;
    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    
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
    @DisplayName("엑세스 토큰 유저 정보 조회시 500 예외 발생")
    void AccessToken_getInfo_ServerEx() throws Exception {
        String fakeToken = "fakeToken";
        given(jwtAuthService.accessTokenMemberInfo(eq(fakeToken))).willThrow(new JwtTokenException("서버 오류발생"));

        mockMvc.perform(post("/api/userinfo")
                        .header("Authorization","Bearer"+fakeToken)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.message").value("500 INTERNAL_SERVER_ERROR \"서버 오류발생\""));
    }
}