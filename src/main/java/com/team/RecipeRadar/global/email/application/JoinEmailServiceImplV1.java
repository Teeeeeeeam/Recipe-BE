package com.team.RecipeRadar.global.email.application;

import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("JoinEmail")
public class JoinEmailServiceImplV1 implements MailService{

    private final JavaMailSender mailSender;
    private String code;
    @Value("${email}")
    private String emailFrom;

    public String sensMailMessage(String email){
        code=createCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("나만의 냉장고 회원가입 인증번호 안내."); // 이메일 제목 설정
        message.setText(getText()); // 이메일 내용 설정
        message.setFrom(emailFrom); // 발신자 이메일 주소 설정
        message.setTo(email); // 수신자 이메일 주소 설정
        mailSender.send(message); // 이메일 발송

        return code;
    }

    private String getText(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("회원 가입을 진행하기 위해서 아래절차를 따라해주세요. "); // 회원 가입 안내 메시지 추가
        buffer.append("아래의 코드를 인증번호 칸에 입력해주세요. "); // 회원 가입 안내 메시지 추가
        buffer.append(code);
        buffer.append(System.lineSeparator()).append(System.lineSeparator()); // 공백 라인 추가
        buffer.append("Regards,").append(System.lineSeparator()).append("나만의 냉장고"); // 문서 마무리 부분 추가
        return buffer.toString(); // 완성된 이메일 내용 반환
    }

    public String createCode(){
        Random random = new Random();
        int key =100000 + random.nextInt(900000);
        return String.valueOf(key);
    }

    public String getCode(){
        return String.valueOf(code);
    }


    /**
     * 회원가입시 이메일 인증번호 유호성 검사
     * @param code
     * @return 인증 성공시 true, 실패시 false
     */
    public Map<String, Boolean> verifyCode(String code){
        Map<String, Boolean> result = new LinkedHashMap<>();
        String realCode = getCode();
        if (realCode.equals(code)){
            result.put("isVerifyCode",true);
        }else throw new BadRequestException("인증번호가 일치하지 않습니다.");

        return result;
    }
}
