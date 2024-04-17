package com.team.RecipeRadar.domain.userInfo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoEmailRequest;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import com.team.RecipeRadar.domain.userInfo.application.UserInfoService;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoUpdateNickNameRequest;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
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

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInfoController.class)
@Slf4j
class UserInfoControllerTest {

    @MockBean
    private UserInfoService userInfoService;
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

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("사용자 정보 조회 API 성공 테스트")
    @CustomMockUser
    public void userInfo_Success() throws Exception {
        // Given
        String loginId = "test";
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        UserInfoResponse expectedResponse = UserInfoResponse.builder()
                .nickName("나만냉장고")
                .username("홍길동")
                .loginType("normal")
                .email("test@naver.com").build();

        given(userInfoService.getMembers(eq(loginId), anyString())).willReturn(expectedResponse);

        // When, Then
        mockMvc.perform(get("/api/user/info/{login-id}", loginId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
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

        Cookie cookie = new Cookie("login-id", "fakeCookie");
        when(userInfoService.getMembers(eq(loginId), anyString()))
                .thenThrow(new AccessDeniedException("Access Denied"));

        // When, Then
        mockMvc.perform(get("/api/user/info/{login-id}", loginId)
                        .cookie(cookie))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }


    @Test
    @DisplayName("사용자 닉네임 업데이트 API 성공 테스트")
    @CustomMockUser
    public void userInfoUpdate_SuccessTest() throws Exception {
        String nickName ="newNickname";
        String loginId = "testId";

        UserInfoUpdateNickNameRequest request = new UserInfoUpdateNickNameRequest();
        request.setNickName(nickName);
        request.setLoginId(loginId);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        mockMvc.perform(put("/api/user/info/update/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("변경 성공"));

        verify(userInfoService).updateNickName(eq(nickName), eq(loginId), anyString());
        verify(userInfoService, times(1)).updateNickName(eq(nickName), eq(loginId), anyString());
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트 API 실패 테스트")
    @CustomMockUser
    public void userInfoUpdate_FailTest() throws Exception {
        String nickName ="newNickname";
        String loginId = "testId";

        UserInfoUpdateNickNameRequest request = new UserInfoUpdateNickNameRequest();
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        request.setNickName(nickName);
        request.setLoginId(loginId);


        doThrow(new AccessDeniedException("접근 불가한 페이지")).when(userInfoService).updateNickName(eq(nickName), eq(loginId), anyString());

        mockMvc.perform(put("/api/user/info/update/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근 불가한 페이지"));
    }

    @Test
    @DisplayName("사용자 페이지 이메일 변경 성공 테스트")
    @CustomMockUser
    void userInfo_Update_Email_Success_Test() throws Exception {
        // Given
        String email = "test@email.com";
        String loginId = "loginId";
        String code = "123456";
        UserInfoEmailRequest request = new UserInfoEmailRequest(email, code, loginId);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        // When, Then
        mockMvc.perform(put("/api/user/info/update/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("변경 성공"));

        verify(userInfoService).updateEmail(eq(email),eq(code),eq(loginId),anyString());
        verify(userInfoService,times(1)).updateEmail(eq(email),eq(code),eq(loginId),anyString());
    }


    @Test
    @DisplayName("사용자 페이지 이메일 변경 실패 테스트")
    @CustomMockUser
    void userInfo_Update_Email_Fail_Test() throws Exception {
        // Given
        String email = "test@email.com";
        String loginId = "loginId";
        String code = "123456";
        UserInfoEmailRequest request = new UserInfoEmailRequest(email, code, loginId);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        doThrow(new BadRequestException("접근할수 없는 페이지 입니다.")).when(userInfoService).updateEmail(eq(email),eq(code),eq(loginId),anyString());

        // When, Then
        mockMvc.perform(put("/api/user/info/update/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근할수 없는 페이지 입니다."));

        verify(userInfoService).updateEmail(eq(email),eq(code),eq(loginId),anyString());
        verify(userInfoService,times(1)).updateEmail(eq(email),eq(code),eq(loginId),anyString());
    }

//    @Test
//    @CustomMockUser
//    void passwordMatch_Success_Cookie() throws Exception {
//        String password = "password";
//        String loginId = "loginId";
//        String auName ="auName";
//        String uuid = "UUID";
//
//        Cookie cookie = new Cookie("login-id", uuid);
//        given(userInfoService.userToken(loginId,auName,password)).willReturn(uuid);
//
//        mockMvc.perform(post("api/user/info/valid")
//                .cookie(cookie)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect("")
//    }
}