package com.team.RecipeRadar.domain.email.application;

import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * 아이디 찾기를 위한 이메일 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Qualifier("AccountEmail")
public class AccountRetrievalEmailServiceImpl implements MailService{

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private int code;

    @Value("${email}")
    private String emailFrom;

    @Override
    public String sensMailMessage(String email) {
        code=createCode();

        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(3);
        EmailVerification emailVerification = EmailVerification.builder()
                .createTime(LocalDateTime.now())
                .lastTime(localDateTime)
                .email(email)
                .code(code).build();
        emailVerificationRepository.save(emailVerification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("나만의 냉장고 이메일 인증 안내."); // 이메일 제목 설정
        message.setText(getText()); // 이메일 내용 설정
        message.setFrom(emailFrom); // 발신자 이메일 주소 설정
        message.setTo(email); // 수신자 이메일 주소 설정
        mailSender.send(message); // 이메일 발송

        return String.valueOf(code);
    }

    private String getText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("안녕하세요,").append(System.lineSeparator());
        buffer.append("나만의 냉장고를 이용해 주셔서 감사합니다.").append(System.lineSeparator());
        buffer.append("이메일 인증을 위한 인증 코드를 안내해 드립니다: ").append(System.lineSeparator());
        buffer.append(code).append(System.lineSeparator()).append(System.lineSeparator());
        buffer.append("인증 코드를 입력해주세요.").append(System.lineSeparator());
        buffer.append("감사합니다.").append(System.lineSeparator()); // 문서 마무리 부분 추가
        return buffer.toString(); // 완성된 이메일 내용 반환
    }


    @Override
    public Integer createCode() {
        Random random = new Random();
        int key =100000 + random.nextInt(900000);
        return key;
    }

    @Override
    public Map<String, Boolean> verifyCode(String email, int code) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        boolean isVerifyCode = false;

        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndCode(email, code);

        if (emailVerification != null && isCodeValid(emailVerification)) {
            isVerifyCode = true;
        } else {
            throw new IllegalStateException("인증번호가 일치하지 않습니다.");
        }

        result.put("isVerifyCode", isVerifyCode);
        return result;
    }

    @Override
    public void deleteCode(String email, int code) {
        emailVerificationRepository.deleteByEmailAndCode(email,code);
    }

    private boolean isCodeValid(EmailVerification emailVerification) {
        LocalDateTime lastTime = emailVerification.getLastTime();
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(lastTime);
    }
}
