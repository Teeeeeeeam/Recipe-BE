package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.UserInfoResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Slf4j
class MemberControllerTest {

    @MockBean
    private MemberService memberService;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    CustomOauth2Handler customOauth2Handler;
    @MockBean
    CustomOauth2Service customOauth2Service;


    @Test
    @DisplayName("사용자 정보 조회 API 성공 테스트")
    @CustomMockUser
    public void userInfo_Success() throws Exception {
        // Given
        String loginId = "test";
        UserInfoResponse expectedResponse = UserInfoResponse.builder()
                .nickName("나만냉장고")
                .username("홍길동")
                .loginType("normal")
                .email("test@naver.com").build();

        given(memberService.getMembers(eq(loginId), anyString())).willReturn(expectedResponse);

        // When, Then
        mockMvc.perform(get("/api/user/info/{login-id}", loginId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회성공"))
                .andExpect(jsonPath("$.data.username").value("홍길동"))
                .andExpect(jsonPath("$.data.nickName").value("나만냉장고"))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(jsonPath("$.data.loginType").value("normal"));
    }

    @Test
    @DisplayName("사용자 정보 조회 API 실패 테스트")
    @CustomMockUser
    public void userInfo_AccessDeniedException() throws Exception {
        // Given
        String loginId = "testId";
        when(memberService.getMembers(eq(loginId), anyString()))
                .thenThrow(new AccessDeniedException("Access Denied"));

        // When, Then
        mockMvc.perform(get("/api/user/info/{login-id}", loginId))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

}