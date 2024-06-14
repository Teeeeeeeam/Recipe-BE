package com.team.RecipeRadar.domain.email.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Qualifier("NoneQuestionEmail")
public class NonUserInquiryResponseEmailSender implements MailService {

    private final JavaMailSender mailSender;
    @Value("${email}")
    private String emailFrom;

    @Override
    public String sendMail(String email, String subject, String body) {

        sendEmailVerification(email, subject, body);

        return "ok";
    }

    /**
     * 메일 전송 메서드
     */
    private void sendEmailVerification(String email, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("나만의 냉장고 이용 제한");
        message.setText(createText(subject, body));
        message.setFrom(emailFrom);
        message.setTo(email);
        mailSender.send(message);
    }

    /**
     * 메일 내용 메서드
     */
    private String createText(String subject, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDate.now()).append("에 문의사항에 대한 답변이 도착했습니다.").append("\n");
        sb.append("주제: ").append(subject).append("\n\n");
        sb.append("내용: ").append(body);
        return sb.toString();
    }

}
