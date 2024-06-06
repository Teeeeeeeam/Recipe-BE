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
@Qualifier("QuestionEmail")
public class InquiryResponseEmailSender implements MailService {

    private final JavaMailSender mailSender;

    @Value("${email}")
    private String emailFrom;

    @Override
    public String sensMailMessage(String email) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("나만의 냉장고 이용 제한");
        message.setText(createText());
        message.setFrom(emailFrom);
        message.setTo(email);
        mailSender.send(message);
        createText();

        return "ok";
    }

    private String createText() {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요 나만의 냉장고 회원님").append("\n");
        sb.append(LocalDate.now()).append(" 문의사항에 대한 답변이 도착했습니다.").append("\n");
        sb.append("마이 페이지에서 문의사항을 확인해주세요.").append("\n");
        sb.append("감사합니다.");
        return sb.toString();
    }

}
