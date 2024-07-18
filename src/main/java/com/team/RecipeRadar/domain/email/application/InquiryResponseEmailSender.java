package com.team.RecipeRadar.domain.email.application;

import lombok.RequiredArgsConstructor;
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
@Qualifier("QuestionEmail")
public class InquiryResponseEmailSender implements MailService {

    private final JavaMailSender mailSender;

    @Value("${email}")
    private String emailFrom;

    @Override
    public String sendMailMessage(String email) {
        SimpleMailMessage message = new SimpleMailMessage();

        sendEmailVerification(email, message);

        return "ok";
    }

    /**
     * 메일 전송 메서드
     */
    private void sendEmailVerification(String email, SimpleMailMessage message) {
        message.setSubject("요리 공유소 문의사항 답변 알림");
        message.setText(getText());
        message.setFrom(emailFrom);
        message.setTo(email);
        mailSender.send(message);
        getText();
    }

    /**
     * 메일 내용 메서드
     */
    private String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요 요리 공유소 회원님").append("\n");
        sb.append(LocalDate.now()).append(" 문의사항에 대한 답변이 도착했습니다.").append("\n");
        sb.append("마이 페이지에서 문의사항을 확인해주세요.").append("\n");
        sb.append("감사합니다.");
        return sb.toString();
    }
}
