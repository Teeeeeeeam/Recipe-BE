package com.team.RecipeRadar.domain.email.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.email.application.AccountRetrievalEmailServiceImpl;
import com.team.RecipeRadar.domain.email.dto.reqeust.EmailVerificationRequest;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Import(SecurityTestConfig.class)
@WebMvcTest(CommonCodeEmailController.class)
class EmailControllerTest {


    @Autowired MockMvc mockMvc;
    @MockBean private AccountRetrievalEmailServiceImpl emailService;
    @MockBean private BlackListRepository blackListRepository;
    
    private final ObjectMapper ob = new ObjectMapper();

    
    @Test
    @DisplayName("이메일 인증번호 전송 테스트")
    void sendVerificationCodeTest() throws Exception {
        when(blackListRepository.existsByEmail("ture@email.com")).thenReturn(false);

        when(emailService.sendMailMessage(anyString())).thenReturn("ok");

        mockMvc.perform(post("/api/code/email-confirmation/send")
                .param("email","test@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("메일 전송 성공"));
    }

    @Test
    @DisplayName("이메일 인증번호 전송시 블랙리스트의 등록된 이메일일 경우")
    void sendVerificationCodeBlackList() throws Exception {
        when(blackListRepository.existsByEmail("test@email.com")).thenReturn(true);

        mockMvc.perform(post("/api/code/email-confirmation/send")
                        .param("email", "test@email.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용할수 없는 이메일입니다."));
    }
    
    @Test
    @DisplayName("이메일 인증코드 검증 테스트")
    void verifyCode() throws Exception {
        when(emailService.verifyCode(anyString(), anyInt())).thenReturn(Map.of("isVerified", true, "isExpired", true));

        EmailVerificationRequest emailVerificationRequest = new EmailVerificationRequest();
        emailVerificationRequest.setEmail("test@email.com");
        emailVerificationRequest.setCode(123456);

        mockMvc.perform(post("/api/code/email-confirmation/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ob.writeValueAsString(emailVerificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증 번호 검증"))
                .andExpect(jsonPath("$.data.isExpired").value(true))
                .andExpect(jsonPath("$.data.isVerified").value(true));
    }

}