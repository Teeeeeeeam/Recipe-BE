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
@Qualifier("ResignEmail")
public class ResignEmailServiceImpl implements MailService{

    private final JavaMailSender mailSender;
    @Value("${email}")
    private String emailFrom;

    @Override
    public String sendMailMessage(String email) {
        SimpleMailMessage message = new SimpleMailMessage();

        sendEmailVerification(email, message);

        return "ok";
    }

    private void sendEmailVerification(String email, SimpleMailMessage message) {
        message.setSubject("나만의 냉장고 이용 제한");
        message.setText(createText());
        message.setFrom(emailFrom);
        message.setTo(email);
        mailSender.send(message);
        createText();
    }

    private String createText(){
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDate.now()).append("시간부로");
        sb.append("당신 탈퇴당했음");
        return sb.toString();
    }
}
