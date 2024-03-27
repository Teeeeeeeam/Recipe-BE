package com.team.RecipeRadar.global.email.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private String code;

    @Value("${email}")
    private String emailFrom;

    @Override
    public String sensMailMessage(String email) {
        code=createCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("나만의 냉장고 아이디 찾기 인증 코드 안내."); // 이메일 제목 설정
        message.setText(getText()); // 이메일 내용 설정
        message.setFrom(emailFrom); // 발신자 이메일 주소 설정
        message.setTo(email); // 수신자 이메일 주소 설정
        mailSender.send(message); // 이메일 발송

        return code;
    }

    private String getText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("안녕하세요,").append(System.lineSeparator());
        buffer.append("나만의 냉장고를 이용해 주셔서 감사합니다.").append(System.lineSeparator());
        buffer.append("아이디 찾기를 위한 인증 코드를 안내해 드립니다: ").append(System.lineSeparator());
        buffer.append(code).append(System.lineSeparator()).append(System.lineSeparator());
        buffer.append("인증 코드를 입력해주세요.").append(System.lineSeparator());
        buffer.append("감사합니다.").append(System.lineSeparator()); // 문서 마무리 부분 추가
        return buffer.toString(); // 완성된 이메일 내용 반환
    }


    @Override
    public String createCode() {
        Random random = new Random();
        int key =100000 + random.nextInt(900000);
        return String.valueOf(key);
    }

    @Override
    public String getCode() {
        return String.valueOf(code);
    }

    /**
     * 아이디찾기  이메일 인증번호 유호성 검사
     * @param code
     * @return 인증 성공시 true, 실패시 false
     */
    public Map<String, Boolean> verifyCode(String code){
        Map<String, Boolean> result = new LinkedHashMap<>();
        String realCode = getCode();
        if (realCode.equals(code)){
            result.put("isVerifyCode",true);
        }else result.put("isVerifyCode",false);

        return result;
    }

}
