package com.team.RecipeRadar.domain.email.application;

import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock private JavaMailSender mailSender;
    @Mock private EmailVerificationRepository emailVerificationRepository;
    @InjectMocks AccountRetrievalEmailServiceImpl accountRetrievalEmailService;


    private EmailVerification emailVerification;
    private EmailVerification expritedemailVerification;
    private String email = "test@example.com";
    private int code = 123456;
    @BeforeEach
    void setUp(){
        emailVerification = EmailVerification.builder().verificationId("testId").email(email).expiredAt(LocalDateTime.now().plusMinutes(3)).build();
        expritedemailVerification = EmailVerification.builder().verificationId("testId").email(email).expiredAt(LocalDateTime.now().minusMinutes(3)).build();
    }

    @Test
    @DisplayName("이메일 전송 및 인증코드 발급 테스트")
    void sendEmail(){
        when(emailVerificationRepository.save(any(EmailVerification.class))).thenReturn(emailVerification);

        String code = accountRetrievalEmailService.sendMailMessage(email);

        assertThat(code).isNotEmpty();
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증코드 검증 테스트- 성공시")
    void verifyCodeTestSuccess() {
        when(emailVerificationRepository.findByEmailAndCode(eq(email), eq(code))).thenReturn(emailVerification);
        
        Map<String, Boolean> result = accountRetrievalEmailService.verifyCode(email, code);
        
        assertThat(result.get("isVerified")).isTrue();
        assertThat(result.get("isExpired")).isTrue();
    }


    @Test
    @DisplayName("인증코드 검증 테스트- 실패시")
    void verifyCodeTest_Fail() {
        when(emailVerificationRepository.findByEmailAndCode(eq(email), eq(code))).thenReturn(expritedemailVerification);

        Map<String, Boolean> result = accountRetrievalEmailService.verifyCode(email, code);

        assertThat(result.get("isVerified")).isTrue();
        assertThat(result.get("isExpired")).isFalse();
    }

    @Test
    @DisplayName("인증코드 삭제 테스트")
    void deleteCodeTest() {
        accountRetrievalEmailService.deleteCode(email, code);

        List<EmailVerification> all = emailVerificationRepository.findAll();
        assertThat(all).isEmpty();
        verify(emailVerificationRepository, times(1)).deleteByEmailAndCode(eq(email), eq(code));
    }


}