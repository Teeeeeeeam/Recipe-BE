package com.team.RecipeRadar.global.email.application;

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

        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("나만의 냉장고 이용 제한");
        message.setText(createText(subject, body));
        message.setFrom(emailFrom);
        message.setTo(email);
        mailSender.send(message);

        return "ok";
    }

    private String createText(String subject, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDate.now()).append("에 문의사항에 대한 답변이 도착했습니다.").append("\n");
        sb.append("주제: ").append(subject).append("\n\n");
        sb.append("내용: ").append(body);
        return sb.toString();
    }

}
