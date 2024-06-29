package com.team.RecipeRadar.domain.member.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.application.user.SinUpService;
import com.team.RecipeRadar.domain.member.dto.rqeust.EmailValidRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.JoinRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.LoginIdValidRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.NicknameValidRequest;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityTestConfig.class)
@WebMvcTest(SingUpValidController.class)
class SingUpValidControllerTest {

    @MockBean SinUpService sinUpService;
    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("회원가입 API - 성공")
    void join_Success() throws Exception {
        JoinRequest request = new JoinRequest();
        request.setUsername("홍길동");
        request.setEmail("test@test.com");
        request.setLoginId("test1234");
        request.setNickname("nickname");
        request.setPassword("asdAsd12!@");
        request.setPasswordRe("asdAsd12!@");
        request.setCode(123456);

        when(sinUpService.ValidationOfSignUp(any())).thenReturn(true);
        doNothing().when(sinUpService).joinMember(any());

        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    @DisplayName("회원가입 API - BingResult_Set")
    void join_Fail() throws Exception {
        JoinRequest request = new JoinRequest();
        request.setUsername("홍길동");
        request.setEmail("test@test.com");
        request.setLoginId("test1234");
        request.setPassword("asdAsd12!@");
        request.setPasswordRe("asdAsd12!@");
        request.setCode(123456);

        when(sinUpService.ValidationOfSignUp(any())).thenReturn(false);
        doNothing().when(sinUpService).joinMember(any());

        mockMvc.perform(post("/api/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.nickname").value("별명을 입력해주세요"));
    }

    @Test
    @DisplayName("로그인아이디 검증 API")
    void loginIdValid() throws Exception {
        String loginId = "test1234";

        when(sinUpService.LoginIdValid(loginId)).thenReturn(any());

        LoginIdValidRequest loginIdValidRequest = new LoginIdValidRequest();
        loginIdValidRequest.setLoginId(loginId);

        mockMvc.perform(post("/api/join/register/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginIdValidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용 가능"));
    }

    @Test
    @DisplayName("이메일 검증 API")
    void emailValid() throws Exception {
        String email = "test@test.com";
        when(sinUpService.emailValid(email)).thenReturn(any());

        EmailValidRequest emailValidRequest = new EmailValidRequest();
        emailValidRequest.setEmail(email);

        mockMvc.perform(post("/api/join/email/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailValidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("이메일 사용 가능"));
    }

    @Test
    @DisplayName("닉네임 검증 API")
    void nickName() throws Exception {
        String nickname = "nickname123";

        doNothing().when(sinUpService).nickNameValid(nickname);
        NicknameValidRequest nicknameValidRequest = new NicknameValidRequest();
        nicknameValidRequest.setNickname(nickname);

        mockMvc.perform(post("/api/join/nickname/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameValidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("닉네임 사용 가능"));
    }
}