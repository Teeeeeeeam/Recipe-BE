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
        sb.append("안녕하세요, 회원님.\n\n");
        sb.append("이 메일은 본 사이트에서의 회원 추방 처리 안내입니다.\n");
        sb.append("아래와 같이 회원 추방 처리가 완료되었음을 안내드립니다:\n\n");
        sb.append("- 추방 시각: ").append(LocalDate.now()).append("\n\n");
        sb.append("회원 추방에 대한 구체적인 이유와 관련된 추가 정보는 본 사이트의 이용 약관을 참조하시거나,\n");
        sb.append("문의 사항이 있으시면 계정문의를 통해 연락주시길 바랍니다.\n\n");
        sb.append("감사합니다.\n");
        sb.append("요리 공유소");
        return sb.toString();
    }
}
